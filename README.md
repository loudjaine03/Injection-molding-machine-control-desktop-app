#  Injection Molding Machine Control App

A Java Swing-based graphical user interface desktop application designed to simulate and monitor the injection molding cycle. It allows users to input process parameters, calculate key timings, and visually track the injection, cooling, and ejection stages in real time.

<img width="960" height="504" alt="image" src="https://github.com/user-attachments/assets/3d8c24f8-b1ff-41d0-b5e2-c50bb7ec6a7a" />



## Project Context

This application was developed as a university lab assignment for the **Human-Machine Interface (HMI) module** at University Dr. Yahia Fares of Médéa. It serves as a practical exercise in designing and implementing HMI solutions for industrial processes.

---

## ⚙️ Features

- 🔧 **User Configuration Panel**:
  - Melt, mold, and ejection temperatures
  - Injection speed and pressure
  - Mold gate dimensions
  - Part size and thickness
  - Material selection from predefined types
- 🖥️ **Real-Time Dashboard**:
  - Displays simulation progress
  - Tracks current cycle stage
  - Progress bar and timer display

---

## 📐 Calculations

- **Injection Time** = (Piece Volume) / (Gate Area × Injection Speed)
- **Cooling Time** = `(Thickness² / (π² × Thermal Diffusivity)) × ln((MoldTemp - EjectTemp) / (MeltTemp - EjectTemp))`
- **Ejection Time:** A fixed value of `2.0 seconds` for simplicity in this simulation.
- **Total Cycle Time** = Injection + Cooling + Ejection


---

## 📄 Materials Data

Material parameters (predefined suggested temperatures and thermal diffusivity) are loaded from an external file:
`materials.txt`

