# Geospatial Engineering Lab

A modular engineering laboratory exploring geodesy algorithms, spatial math, and high-performance computing built with Kotlin.

## Overview

This repository is organized as a Gradle monorepo designed to experiment with, test, and implement core geospatial transformations from first principles—prioritizing pure, idiomatic Kotlin without heavy external dependencies.

## Project Structure

```text
geospatial-engineering-lab/
├── 01-math-and-geodesy/       # Ellipsoidal geometry, ECEF, and geodetic transformations
├── 02-local-frames-enu/       # Local tangent plane (East, North, Up) transformations
└── 03-utm-projections/        # Transverse Mercator and UTM grid projections
```

## Modules

### `01-math-and-geodesy`
* **WGS84 Datum Model:** Ellipsoidal constants including semi-major axis ($a$), flattening ($f$), and eccentricity squared ($e^2$).
* **Direct Transformation:** Geodetic coordinates $(\phi, \lambda, h) \to \text{ECEF } (X, Y, Z)$.
* **Inverse Transformation:** ECEF $\to$ Geodetic conversion using Bowring's closed-form algorithm.
* **Interactive CLI:** Terminal loop with sanitized numerical input handling regional decimal formatting.
* **Automated Tests:** Comprehensive unit test suite using `kotlin.test` verifying sub-millimeter roundtrip accuracy and prime vertical radius calculations.
  
### `02-local-frames-enu`
* **Local Tangent Plane Model:** `ENU` data class representing metric offsets (East, North, Up) relative to a local reference point.
* **Direct Transformation:** ECEF $\to$ ENU projection applying the local rotation matrix based on the anchor's geodetic coordinates ($\phi, \lambda$).
* **Inverse Transformation:** ENU $\to$ ECEF reconstruction using the transpose matrix and Euclidean offset aggregation.
* **Interactive CLI:** Terminal loop supporting bi-directional conversion with anchor/target coordinate prompts and input validation.
* **Automated Tests:** Comprehensive unit test suite using `kotlin.test` verifying origin zeroing and sub-millimeter roundtrip consistency.

### `03-utm-projections`
* **Cartographic Projection Model:** `UTMCoordinates` data class encapsulating Easting, Northing, Zone (1-60), and Hemisphere (North/South).
* **Direct Transformation:** Geodetic coordinates $(\phi, \lambda) \to \text{UTM } (E, N)$ using Redfearn's transverse ellipsoidal series expansion up to 6th order.
* **Inverse Transformation:** UTM $\to$ Geodetic coordinates applying the footprint latitude ($\phi_1$) derived from rectifying sphere geometry and inverse Taylor corrections.
* **Interactive CLI:** Terminal application allowing seamless forward/inverse UTM conversion with coordinate validation and hemisphere handling.
* **Automated Tests:** Comprehensive unit test suite validating numerical roundtrip consistency across equatorial and mid-latitude boundaries.
  
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
