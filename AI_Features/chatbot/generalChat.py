import os
from dotenv import load_dotenv
from langchain_groq import ChatGroq
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage
from langchain_core.output_parsers import StrOutputParser
from rich import print
from datetime import date

load_dotenv()

systemMessage = """
You are EduSmart AI, a friendly, intelligent, and helpful general-purpose assistant.

Your responsibilities include:
- Answering questions across academic, technical, and general domains.
- Assisting with coding, software development, and problem-solving.
- Explaining complex concepts in a clear and simple manner.
- Providing career guidance and learning advice when requested.
- Engaging in natural and meaningful conversations.

Guidelines:
- Respond clearly, accurately, and concisely.
- Maintain a professional yet friendly tone.
- Provide structured answers when helpful.
- Ask for clarification if a query is ambiguous.
- Do not fabricate information—be honest when unsure.
- Adapt your responses to suit the user's needs.
- Be like an educated, friendly teacher who explains concepts
  patiently and encourages students.

Your goal is to educate, assist, and support users effectively
in any conversation.
"""

teacher_prompt = ChatPromptTemplate.from_messages([
    SystemMessage(content=systemMessage),
    MessagesPlaceholder(variable_name="history"),
    ("human", """
Current Date: {current_date}

User Query:
{user_query}
""")
])

model = ChatGroq(
    model=os.getenv("GROQ_MODEL", "llama-3.3-70b-versatile"),
    temperature=0
)

parser = StrOutputParser()

chain = teacher_prompt | model | parser

history = []
MAX_HISTORY = 10

print("----------Welcome to EduSmart AI Teacher----------\n")
print("----------Type 0 to exit----------\n")
print("----------------Type 'clear' to reset conversation.-----------------\n")

while True:
    question = input("YOU: ").strip()

    if question == "0":
        print("👋 Goodbye! Stay consistent and motivated!")
        break

    if question.lower() == "clear":
        history.clear()
        print("✅ Chat history cleared.\n")
        continue

    if not question:
        continue

    input_data = {
        "current_date": date.today().isoformat(),
        "user_query": question,
        "history": history
    }

    try:
        response = chain.invoke(input_data)

        history.append(HumanMessage(content=question))
        history.append(AIMessage(content=response))

        print("\n[bold cyan]AI Teacher:[/bold cyan]\n")
        print(response)
        print()

        # Keep the latest 10 messages (5 conversation turns)
        history = history[-MAX_HISTORY:]

    except Exception as e:
        print(f"❌ Error: {e}")