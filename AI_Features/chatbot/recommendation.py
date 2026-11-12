from dotenv import load_dotenv
import os
from datetime import date

from langchain_groq import ChatGroq
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage
from langchain_core.output_parsers import StrOutputParser

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

model = ChatGroq(
    model=os.getenv("GROQ_MODEL", "llama-3.3-70b-versatile"),
    temperature=0
)

parser = StrOutputParser()
chain = teacher_prompt | model | parser

history = []
MAX_HISTORY = 10

print("----------Welcome to EduSmart AI Planner----------\n")
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
        "tests": os.getenv("TESTS", "None"),
        "assignments": os.getenv("ASSIGNMENTS", "None"),
        "timetable": os.getenv("TIMETABLE", "Not provided"),
        "holidays": os.getenv("HOLIDAYS", "None"),
        "user_response": os.getenv("USER_STATUS", ""),
        "career_goal": os.getenv("CAREER_GOAL", "Software Engineer"),
        "preferred_activities": os.getenv(
            "PREFERRED_ACTIVITIES",
            "Coding, Problem Solving"
        ),
        "available_hours": os.getenv("AVAILABLE_HOURS", "3 hours"),
        "user_query": question,
        "history": history
    }

    try:
        response = chain.invoke(input_data)

        history.append(HumanMessage(content=question))
        history.append(AIMessage(content=response))
        history = history[-MAX_HISTORY:]

        print("\n[bold cyan]AI Coach:[/bold cyan]\n")
        print(response)
        print()

    except Exception as e:
        print(f"❌ Error: {e}")