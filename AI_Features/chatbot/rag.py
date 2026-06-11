from dotenv import load_dotenv
import os
from langchain_mistralai.chat_models import ChatMistralAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.messages import SystemMessage,HumanMessage,AIMessage
from langchain_core.prompts import ChatPromptTemplate,MessagesPlaceholder
from langchain_core.output_parsers import StrOutputParser

from langchain_mistralai import MistralAIEmbeddings
from langchain_community.vectorstores import Chroma

from rich import print

load_dotenv()


BASE_DB_DIR = "chromadb"
name='keshav'



systemMessage = """
You are EduSmart AI, a Retrieval-Augmented Generation (RAG) assistant.

Your role is to answer questions strictly using the provided retrieved context from uploaded documents.

RULES:
- Use ONLY the retrieved context to generate answers.
- Do NOT use prior knowledge or make assumptions.
- Do NOT hallucinate or fabricate information.
- If the answer is not found in the retrieved context, respond with:
  "The answer is not available in the provided document."
- Provide clear, concise, and accurate explanations.
- When relevant, summarize or explain concepts based solely on the context.
- Always end your response with:
  "Source: Retrieved Document."
"""

teacher_prompt = ChatPromptTemplate.from_messages([
    SystemMessage(content=systemMessage),
    MessagesPlaceholder(variable_name="history"),
    ("human", """
Retrieved Context:
{retrieved_context}

User Query:
{user_query}
""")
])



def load_user_vectorstore(user_id: str):
    user_db_path = get_user_db_path(user_id)

    if not os.path.exists(user_db_path):
        raise FileNotFoundError(
            f"No database found for user '{user_id}'. Please upload a document first."
        )

    embeddings = MistralAIEmbeddings()

    return Chroma(
        persist_directory=user_db_path,
        embedding_function=embeddings
    )


def get_user_db_path(user_id: str) -> str:
    return os.path.join(BASE_DB_DIR, user_id)



def get_retrieved_context(query: str, user_id: str) -> str:
    try:
        vectorstore = load_user_vectorstore(user_id)
        retriever = vectorstore.as_retriever(
            search_type="mmr",
            search_kwargs={"k": 3, "fetch_k": 6}
        )

        docs = retriever.invoke(query)
        return "\n\n".join(doc.page_content for doc in docs) if docs else ""
    except Exception as e:
        return ""



model = ChatMistralAI()
parser = StrOutputParser()


chain = teacher_prompt | model | parser

history = []
print("----------Welcome to AI Teacher----------\n")
print("----------Type 0 to exit----------\n")
print("----------------Type 'clear' to reset conversation.-----------------\n")
while True:

    question = input("YOU: ")

    if question.lower() == "0":
        print("👋 Goodbye! Stay consistent and motivated!")
        break

    if question.lower() == "clear":
        history.clear()
        print("✅ Chat history cleared.\n")
        continue

    ret_con = get_retrieved_context(question,name)

    input_data = {
        "retrieved_context": ret_con,
        "user_query": question,
        "history": history
    }

    try:
        # Generate response using the chain
        response = chain.invoke(input_data)

        # Store messages in history
        history.append(HumanMessage(content=question))
        history.append(AIMessage(content=response))

        # Display AI response
        print("\nAI Coach:\n")
        print(response)
        print()

        # Limit history to avoid token overflow
        MAX_HISTORY = 10
        history = history[-MAX_HISTORY:]

    except Exception as e:
        print(f"❌ Error: {e}")
