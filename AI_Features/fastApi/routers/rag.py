from fastapi import APIRouter, Depends, HTTPException
from langchain_mistralai import MistralAIEmbeddings
from langchain_community.vectorstores import Chroma

from utils.auth import get_current_user
from models.requests import ChatRequest
from chains.rag_chain import rag_chain
from chroma_connection import get_chroma_client

router = APIRouter()


def load_user_vectorstore(user_id: str, user_type: str):
    embeddings = MistralAIEmbeddings()
    collection_name = (
        f"{user_type}_{user_id}"
        .lower()
        .replace("@", "_")
        .replace(".", "_")
    )
    return Chroma(
        client=get_chroma_client(),
        collection_name=collection_name,
        embedding_function=embeddings
    )


def get_retrieved_context(query: str, user_id: str, user_type: str):
    vectorstore = load_user_vectorstore(user_id, user_type)
    retriever = vectorstore.as_retriever(
        search_type="mmr",
        search_kwargs={"k": 4, "fetch_k": 20}
    )
    docs = retriever.invoke(query)
    return "\n\n".join(doc.page_content for doc in docs) if docs else ""


@router.post("/rag")
def chat(
    request: ChatRequest,
    current_user: dict = Depends(get_current_user)
):
    try:
        retrieved_context = get_retrieved_context(
            request.query,
            current_user["email"],
            current_user["user_type"]
        )

        response = rag_chain.invoke({
            "retrieved_context": retrieved_context,
            "user_query": request.query,
            "history": request.history
        })

        return {
            "response": response,
        }

    except FileNotFoundError:
        raise HTTPException(status_code=404, detail="No document uploaded for this user")

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))