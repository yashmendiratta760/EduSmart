from dotenv import load_dotenv
from langchain_mistralai.chat_models import ChatMistralAI
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage
from langchain_core.output_parsers import StrOutputParser
from rich import print

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

Your goal is to educate, assist, and support users effectively in any conversation.


Also be a little educated and firendly teacher kind of bot.
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

    input_data = {
    "current_date": "2026-04-11",
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
