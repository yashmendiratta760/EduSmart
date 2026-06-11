from dotenv import load_dotenv
import os
import shutil
from langchain_mistralai import MistralAIEmbeddings
from langchain_community.vectorstores import Chroma
from langchain_community.document_loaders import PyPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter

load_dotenv()

BASE_DB_DIR = "chromadb"
name='yash' #parameter
path = 'docs/Resume_yash _updated.pdf'  #ask from user (also user cqn give any type of file odf/doc/jpg/txt adjust my code according to that)

def get_user_db_path(user_id: str) -> str:
    return os.path.join(BASE_DB_DIR, user_id)



def reset_user_vectorstore(user_id: str):
    user_db_path = get_user_db_path(user_id)

    if os.path.exists(user_db_path):
        shutil.rmtree(user_db_path)
        print(f"✅ Previous database deleted for user: {user_id}")

    os.makedirs(user_db_path, exist_ok=True)
    return user_db_path



def create_vectorstore_from_pdf(pdf_path: str, user_id: str):
    # Reset the user's database
    user_db_path = reset_user_vectorstore(user_id)

    # Load the document
    loader = PyPDFLoader(pdf_path)
    documents = loader.load()

    # Split the document into chunks
    splitter = RecursiveCharacterTextSplitter(
        chunk_size=800,
        chunk_overlap=100
    )
    docs = splitter.split_documents(documents)

    # Generate embeddings
    embeddings = MistralAIEmbeddings()

    # Create and persist vector database
    vectorstore = Chroma.from_documents(
        documents=docs,
        embedding=embeddings,
        persist_directory=user_db_path
    )

    vectorstore.persist()
    print(f"✅ New vector database created for user: {user_id}")

    return vectorstore


create_vectorstore_from_pdf(path,name)


