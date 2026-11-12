from dotenv import load_dotenv
import os
import shutil

from langchain_groq import ChatGroq
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from langchain_chroma import Chroma
from langchain_community.document_loaders import PyPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter

load_dotenv()

BASE_DB_DIR = "chromadb"

name = "yash"  # User ID
path = "docs/Resume_yash _updated.pdf"


def get_user_db_path(user_id: str) -> str:
    return os.path.join(BASE_DB_DIR, user_id)


def reset_user_vectorstore(user_id: str):
    user_db_path = get_user_db_path(user_id)

    if os.path.exists(user_db_path):
        shutil.rmtree(user_db_path)
        print(f"Previous database deleted for user: {user_id}")

    os.makedirs(user_db_path, exist_ok=True)
    return user_db_path


def create_vectorstore_from_pdf(pdf_path: str, user_id: str):
    if not os.path.isfile(pdf_path):
        raise FileNotFoundError(f"PDF not found: {pdf_path}")

    user_db_path = reset_user_vectorstore(user_id)

    loader = PyPDFLoader(pdf_path)
    documents = loader.load()

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=800,
        chunk_overlap=100
    )
    docs = splitter.split_documents(documents)

    # Hosted Gemini embeddings; no local PyTorch required.
    embeddings = GoogleGenerativeAIEmbeddings(
    model="gemini-embedding-001",
    output_dimensionality=1024
)

    vectorstore = Chroma.from_documents(
        documents=docs,
        embedding=embeddings,
        persist_directory=user_db_path
    )

    print(f"New vector database created for user: {user_id}")
    return vectorstore


# Groq LLM
llm = ChatGroq(
    model=os.getenv("GROQ_MODEL", "llama-3.3-70b-versatile"),
    temperature=0
)


if __name__ == "__main__":
    vectorstore = create_vectorstore_from_pdf(path, name)

    query = "What are the technical skills in this resume?"
    docs = vectorstore.similarity_search(query, k=3)

    context = "\n\n".join(doc.page_content for doc in docs)

    response = llm.invoke(
        f"Answer using only the following PDF context. "
        f"If the answer is missing, say so.\n\n"
        f"Context:\n{context}\n\n"
        f"Question: {query}"
    )

    print(response.content)