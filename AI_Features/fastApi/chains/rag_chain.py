from langchain_mistralai.chat_models import ChatMistralAI
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.messages import SystemMessage
from langchain_core.output_parsers import StrOutputParser

SYSTEM_MESSAGE = """
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

rag_prompt = ChatPromptTemplate.from_messages([
    SystemMessage(content=SYSTEM_MESSAGE),
    MessagesPlaceholder(variable_name="history"),
    ("human", """
Retrieved Context:
{retrieved_context}

User Query:
{user_query}
""")
])

model = ChatMistralAI()
parser = StrOutputParser()

rag_chain = rag_prompt | model | parser