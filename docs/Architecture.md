# Architecture Blueprint

## Core Principles
1. **Unidirectional Data Flow (UDF)**: UI -> ViewModel -> Use Case -> Repository -> Database
2. **Feature Isolation**: Each feature (Dashboard, Focus, Planner) operates independently.
3. **Interface-Driven Communication**: Modules only communicate via Repository interfaces, never accessing another module's internal state directly.
4. **Result Wrapper**: All repository operations return a `Result<T>` sealed class (`Loading`, `Success`, `Error`).

## Folder Structure
We follow a feature-first package structure:
- `core/`: Reusable utilities, extensions, and common UI elements.
- `feature_*/`: Isolated feature modules (ui, viewmodel, domain).
- `data/`: Room database, DAO, local data sources, and Repository implementations.
- `domain/`: Business logic, Use Cases, and Repository interfaces.
- `design_system/`: Global theme components (colors, typography, spacing).

## Repositories
Repositories hide implementation details from the UI.
- FocusRepository
- PlannerRepository
- AnalyticsRepository
- UsageRepository
- AIRepository
