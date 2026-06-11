import os
import uuid
import requests
from urllib.parse import urlparse

from fastapi import APIRouter, Depends
from langchain_mistralai import MistralAIEmbeddings
from langchain_community.vectorstores import Chroma
from langchain_community.document_loaders import (
    PyPDFLoader,
    TextLoader,
    UnstructuredWordDocumentLoader,
    UnstructuredImageLoader,
)
from langchain_text_splitters import RecursiveCharacterTextSplitter

from utils.auth import get_current_user
from chroma_connection import get_chroma_client

router = APIRouter()


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
    file_url: str,
    current_user: dict = Depends(get_current_user)
):
    temp_file = None
    try:
        response = requests.get(str(file_url))
        response.raise_for_status()

        extension = os.path.splitext(
            urlparse(str(file_url)).path
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

        embeddings = MistralAIEmbeddings()

        collection_name = (
            f"{current_user['user_type']}_{current_user['email']}"
            .replace("@", "_")
            .replace(".", "_")
        )

        client = get_chroma_client()
        try:
            client.delete_collection(collection_name)
        except Exception:
            pass

        vectorstore = Chroma(
            client=client,
            collection_name=collection_name,
            embedding_function=embeddings
        )
        vectorstore.add_documents(docs)

        return {
            "status": "success",
            "message": f"Vector DB created for user {current_user['email']}",
            "filename": unique_filename,
            "chunks": len(docs)
        }

    except Exception as e:
        return {"status": "error", "message": str(e)}

    finally:
        if temp_file and os.path.exists(temp_file):
            os.remove(temp_file)