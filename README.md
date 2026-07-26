# ThessTransit

<div align="justify", text>
  <strong>
    <font size="5">
      ThessTransit is an intelligent urban mobility application designed for Thessaloniki, combining real-time data from OASTH buses, the Metro system, news     sources, and traffic patterns to provide users with the fastest and most efficient routes across the city. Powered by LLMs and predictive analytics, the app can detect disruptions, analyze transportation trends, and dynamically optimize routes based on live conditions. It also serves as a useful guide for anyone using public transportation in Thessaloniki, providing information and guides on various aspects of the city's transport network.
    </font>
  </strong>
</div>

---

## Getting Started

To download the application:

1) Download the latest release APK (version 0.9.1) from **[HERE](https://github.com/Nikos1508/ThessTransit/releases/download/v0.9.1/app-debug.apk)**.
2. Open the app and wait approximately **2–3 minutes** while all routes, stops, and timetable information are downloaded and stored locally.
3) Enjoy the application! More updates and features will be announced here as development continues.

---

## About the Project

The project is mainly focused on creating a modern transportation application for Thessaloniki that combines both the bus and metro systems while incorporating LLMs and AI agents to improve the user experience. The long-term goal fot the project is to provide more reliable route suggestions, adjusted arrival times, live news updates, and all the information a passenger may need in one place (including guides on various different aspects)

The application is primarily developed by me, for now, (Hallooo), with AI being used only as a development assistant for tasks such as generating XML string resources, debugging errors, and speeding up parts of the development process with the repeating parts of the code with minimal changes between them.

---

## Features available in Version 0.9

- Fully working **offline mode**, providing access to all routes and stops without an internet connection.
- Local storage of user preferences (theme, language, favourite routes, home, work, etc.).
- Fully functional Home Screen UI.
- Detailed ticket information on the Tickets screen.
- Maps for selecting your home and work locations to easily compare nearby bus stops *(partially unavailable due to map access issues)*.
- Complete route information, including stops, timetables, and their locations on the map.
- Settings page with options for changing the application theme and language.
- Grouped routes (based on route numbers) for easier navigation towards different areas of Thessaloniki.
- Working Login screen UI.
- Early version of the route search page.

<br>

<div align="center">
<img width="144" height="320" alt="signal-2026-07-25-22-31-09-445_009" src="https://github.com/user-attachments/assets/3fce7a8c-0254-4af0-b31f-3b4b6dd5a8b0" />
<img width="144" height="320" alt="signal-2026-07-25-22-31-09-445_003" src="https://github.com/user-attachments/assets/523dc358-e5bc-4552-9c86-9f0349538c5b" />
<img width="144" height="320" alt="signal-2026-07-25-22-31-09-445_006" src="https://github.com/user-attachments/assets/04fe7d33-8944-4967-92e9-9f66550b7de7" />
<img width="144" height="320" alt="signal-2026-07-25-22-31-09-445_007" src="https://github.com/user-attachments/assets/38edb51b-a900-4f70-9761-c47fd452661e" />
<img width="144" height="320" alt="signal-2026-07-25-22-31-09-445" src="https://github.com/user-attachments/assets/a2bc4af0-e9f2-47b1-9efd-51dbd07b0c05" />
</div>

<br>

---

## Planned Features for Versions 1.0 - 1.1

- Interactive onboarding tutorial on the Home Screen for new users.
- Fully functional login system with cloud synchronization for themes, favourite routes, language preferences, and more.
- Fully working maps without access issues.
- Improved map search results.
- Functional search screen supporting transfers between routes.
- Working notifications system.

---

## Future Vision

- AI Agents for intelligent route planning and faster journeys.
- Voice guidance for improved accessibility, especially for elderly users.
- Custom-designed logos, icons, and UI assets.
- Metro line information, live locations, and complete timetables.

---

## Technologies & Languages Used

The project is primarily developed using **Kotlin** with **Android Studio** and **Jetpack Compose** for building the application interface. 

The project also uses:
- **Gradle Kotlin DSL** with **Version Catalogs (`libs.versions.toml`)** for dependency and build management.
- **SQL databases** for storing and managing structured transportation data, although not seen in this repository as they are mostly used in the api used in this project, which can be found **[HERE](https://github.com/GalaxyGamingBoy/kt-bus)**
- **REST APIs** for communication with transportation services and retrieving live information (the one above)

In future versions, **Python** will be introduced for AI-related features, including AI agents and intelligent route optimization
