import chromadb
from chromadb.api import ClientAPI
from chromadb.api.models.Collection import Collection
from fastapi import Depends
from dotenv import load_dotenv
import os

load_dotenv()

_client: ClientAPI | None = None
_collection: Collection | None = None

def get_collection_name(user_type: str, email: str) -> str:
    safe_email = email.replace("@", "_").replace(".", "_")
    return f"{user_type}_{safe_email}"

def get_chroma_client() -> ClientAPI:
	global _client
	if _client is None:
		_client = chromadb.CloudClient(
            api_key=os.getenv("CHROMA_API_KEY"),
            tenant=os.getenv("CHROMA_TENANT"),
            database=os.getenv("CHROMA_DATABASE")
        )
	return _client

def get_chroma_collection(
    user_type: str,
    email: str,
    client: ClientAPI
) -> str:

    collection_name = (
        f"{user_type}_{email}"
        .lower()
        .replace("@", "_")
        .replace(".", "_")
    )

    return collection_name