# Geospatial Engineering Lab

A modular engineering laboratory exploring geodesy algorithms, spatial math, and high-performance computing built with Kotlin.

## Overview

This repository is organized as a Gradle monorepo designed to experiment with, test, and implement core geospatial transformations from first principles—prioritizing pure, idiomatic Kotlin without heavy external dependencies.

## Project Structure

```text
geospatial-engineering-lab/
├── 01-math-and-geodesy/       # Ellipsoidal geometry, ECEF, and geodetic transformations
├── 02-local-frames-enu/      # (Upcoming) Local tangent plane (East, North, Up) transformations
└── 03-utm-projections/       # (Roadmap) Transverse Mercator and UTM grid projections
```

## Modules

### `01-math-and-geodesy`
* **WGS84 Datum Model:** Ellipsoidal constants including semi-major axis ($a$), flattening ($f$), and eccentricity squared ($e^2$).
* **Direct Transformation:** Geodetic coordinates $(\phi, \lambda, h) \to \text{ECEF } (X, Y, Z)$.
* **Inverse Transformation:** ECEF $\to$ Geodetic conversion using Bowring's closed-form algorithm.
* **Interactive CLI:** Terminal loop with sanitized numerical input handling regional decimal formatting.
* **Automated Tests:** Comprehensive unit test suite using `kotlin.test` verifying sub-millimeter roundtrip accuracy and prime vertical radius calculations.

## Tech Stack

* **Language:** Kotlin (JVM 17+)
* **Build Tool:** Gradle (Kotlin DSL)
* **Testing:** `kotlin.test` (JUnit 5 Platform)

## Getting Started

### Run Tests
Execute the automated test suite across all modules:
```bash
./gradlew test
```

### Run the Interactive CLI
Launch the terminal converter application:
```bash
./gradlew :01-math-and-geodesy:run --console=plain
```
