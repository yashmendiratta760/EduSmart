import os
import uuid
import requests
from urllib.parse import urlparse
from dotenv import load_dotenv

from fastapi import APIRouter, Depends, HTTPException
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from langchain_community.vectorstores import Chroma
from langchain_community.document_loaders import (
    PyPDFLoader,
    TextLoader,
    UnstructuredWordDocumentLoader,
    UnstructuredImageLoader,
)
from langchain_text_splitters import RecursiveCharacterTextSplitter

from models.requests import UploadRequest
from utils.auth import get_current_user
from chroma_connection import get_chroma_client, get_chroma_collection

router = APIRouter()

load_dotenv()

embeddings = GoogleGenerativeAIEmbeddings(
    model="gemini-embedding-001",
    output_dimensionality=1024
)


def get_loader(file_path: str, file_ext: str):
    if file_ext == ".pdf":
        return PyPDFLoader(file_path)
    elif file_ext == ".txt":
        return TextLoader(file_path)
    elif file_ext in [".doc", ".docx"]:
        return UnstructuredWordDocumentLoader(file_path)
    elif file_ext in [".jpg", ".jpeg", ".png"]:
        return UnstructuredImageLoader(file_path)
    else:
        raise ValueError(f"Unsupported file type: {file_ext}")


@router.post("/upload")
async def upload_file(
    request: UploadRequest,
    current_user: dict = Depends(get_current_user)
):
    temp_file = None

    try:
        file_url = str(request.file_url)

        response = requests.get(file_url, timeout=30)
        response.raise_for_status()

        extension = os.path.splitext(
            urlparse(file_url).path
        )[1].lower()

        unique_filename = f"{uuid.uuid4()}{extension}"
        temp_file = f"/tmp/{unique_filename}"

        with open(temp_file, "wb") as f:
            f.write(response.content)

        loader = get_loader(temp_file, extension)
        documents = loader.load()

        splitter = RecursiveCharacterTextSplitter(
            chunk_size=800,
            chunk_overlap=100
        )

        docs = splitter.split_documents(documents)

        client = get_chroma_client()

        collection_name = get_chroma_collection(
            current_user["user_type"],
            current_user["email"],
            client
        )

        # Replace the existing collection for this user.
        try:
            client.delete_collection(name=collection_name)
        except Exception:
            pass

        vectorstore = Chroma(
            client=client,
            collection_name=collection_name,
            embedding_function=embeddings
        )

        if docs:
            vectorstore.add_documents(docs)

        collection = client.get_collection(collection_name)

        print("Chunks stored:", collection.count())

        return {
            "status": "success",
            "message": (
                f"Vector DB created for user "
                f"{current_user['email']}"
            ),
            "filename": unique_filename,
            "chunks": len(docs)
        }

    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=str(e)
        )

    finally:
        if temp_file and os.path.exists(temp_file):
            os.remove(temp_file)