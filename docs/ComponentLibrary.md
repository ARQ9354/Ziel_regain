# Component Library & Reusable UI Components

## Core Principles
1. **Consistency**: Reusable components instead of duplicating code.
2. **Maintainability**: Centralized changes.
3. **Accessibility**: Every component must provide content descriptions, proper focus, and dynamic scaling.

## Component Hierarchy (Atomic Design)
- **Atoms**: Primary Button, Icon Button, Text, Divider, Chip.
- **Molecules**: Task Card, Statistic Tile, Profile Row, Setting Item.
- **Organisms**: Dashboard Header, Focus Timer, Planner Timeline, Analytics Chart.
- **Templates / Screens**: Full UI screens utilizing the above elements.

## Guidelines
- Components accept immutable state and expose callbacks (`onAction: () -> Unit`).
- Do not hardcode design values; use `MaterialTheme` and centralized tokens from `design_system/theme`.
- Support loading, success, empty, and error states natively.
