# Geospatial Engineering Lab

[![Geospatial Engine CI](https://github.com/arthurriosprogramador/geospatial-engineering-lab/actions/workflows/ci.yml/badge.svg)](https://github.com/arthurriosprogramador/geospatial-engineering-lab/actions/workflows/ci.yml)

A modular engineering laboratory exploring geodesy algorithms, spatial math, and high-performance computing built with Kotlin.

## Overview

This repository is organized as a Gradle monorepo designed to experiment with, test, and implement core geospatial transformations from first principles—prioritizing pure, idiomatic Kotlin without heavy external dependencies.

## Project Structure

```text
geospatial-engineering-lab/
├── core/                             # Shared planar models, geodesy engine, and parsers
├── 01-math-and-geodesy/              # Ellipsoidal geometry, ECEF, and geodetic transformations
├── 02-local-frames-enu/              # Local tangent plane (East, North, Up) transformations
├── 03-utm-projections/               # Transverse Mercator and UTM grid projections
└── 04-computational-geometry/        # Computational geometry with 2D forms
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

### `04-computational-geometry`
* **2D Planar Models:** `Point2D`, `Vector2D`, `Segment2D`, `LinearRing2D`, `Polygon2D`, and `BoundingBox2D` representing planar primitives and topological boundaries.
* **Distance & Displacement Vector:** Computes point-to-point Euclidean distances, vector subtractions, dot products, and directional displacements.
* **Segment Length & Point Orientation:** Computes segment magnitude and evaluates relative point orientation (Left/CCW, Right/CW, Collinear) via 2D cross product.
* **Ring/Polygon Area (Shoelace & Winding Order):** Implements Green's theorem (Shoelace formula) to compute signed areas, detect vertex winding orders, and calculate net polygon area supporting multiple interior rings (holes).
* **Point-in-Polygon Containment (Ray Casting):** Validates point containment across complex topologies with interior holes using ray casting, half-open vertical interval rules, and $O(1)$ bounding box pre-filtering.
* **Automated Tests:** Comprehensive unit test suite validating spatial containment, vertex edge cases, and net area calculations.
  
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
./gradlew :02-local-frames-enu:run --console=plain
./gradlew :03-utm-projections:run --console=plain
./gradlew :04-computational-geometry:run --console=plain
```
