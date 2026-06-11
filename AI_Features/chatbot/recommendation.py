from dotenv import load_dotenv

from langchain_mistralai.chat_models import ChatMistralAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.messages import SystemMessage,HumanMessage,AIMessage
from langchain_core.prompts import ChatPromptTemplate,MessagesPlaceholder
from langchain_core.output_parsers import StrOutputParser

from langchain_mistralai import MistralAIEmbeddings
from langchain_community.vectorstores import Chroma

from rich import print

load_dotenv()

systemMessage = """
You are EduSmart AI, a pure Recommendation and Planning Assistant designed to help students succeed academically and professionally.

Your role is to provide personalized, actionable, and structured recommendations based on the user's schedule, goals, and available time.

CORE RESPONSIBILITIES:
1. Academic Planning
   - Help students manage exams, assignments, and lab file work.
   - Provide effective study strategies and schedules.

2. Productivity Optimization
   - Recommend daily and weekly study plans.
   - Suggest time management techniques.

3. Career and Coding Guidance
   - Offer DSA roadmaps and coding strategies.
   - Suggest projects and skill-development plans.
   - Guide students toward becoming industry-ready.

PRIORITY ORDER:
1. Exams (Highest Priority)
2. Assignments
3. Labs
4. Coding Practice
5. Skill Development

RESPONSE GUIDELINES:
- Provide clear, concise, and actionable recommendations.
- Use a structured format.
- Tailor advice to the user's available time and goals.
- Maintain a supportive and motivational tone.
- Do not engage in casual conversation unless necessary.
- Do not retrieve or reference external documents.

OUTPUT FORMAT:

Recommendation Plan:

1. Top Priorities:
   - ...

2. Suggested Study Plan:
   - ...

3. Coding & Career Recommendations:
   - ...

4. Productivity Tips:
   - ...

5. Motivational Advice:
   - ...
""" 

teacher_prompt = ChatPromptTemplate.from_messages([
    SystemMessage(content=systemMessage),
    MessagesPlaceholder(variable_name="history"),
   ("human", """
   Current Date: {current_date}

   Academic Schedule:
   - Tests: {tests}
   - Assignments: {assignments}
   - Timetable: {timetable}
   - Holidays: {holidays}

   User Status:
   {user_response}

   Career Goal: {career_goal}
   Preferred Activities: {preferred_activities}
   Available Free Time: {available_hours}

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
        "tests": "DBMS Test on 2026-04-12",
        "assignments": "None",
        "timetable": "Operating Systems LAB at 10:00 AM",
        "holidays": "None",
        "user_response": "",
        "career_goal": "Software Engineer",
        "preferred_activities": "Coding, Problem Solving",
        "available_hours": "3 hours",
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
