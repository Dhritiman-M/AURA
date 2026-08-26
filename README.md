# AURA - Autonomous User Runtime Agent

AURA is an autonomous AI agent system designed to assist users with complex tasks through intelligent planning, execution, and learning capabilities.

## Project Structure

```
AURA/
├── android/          # Android client application
├── ai/               # AI core (Python-based)
├── docs/             # Documentation
├── scripts/          # Build and deployment scripts
├── .gitignore
├── README.md
└── docker-compose.yml
```

## Components

### Android App (`/android`)
Native Android client built with Kotlin and Jetpack Compose.
- Minimum SDK: 24
- Target SDK: 34
- Language: Kotlin

### AI Core (`/ai`)
Python-based autonomous agent system with:
- **Agent**: Core agent loop and decision making
- **Planner**: Task planning and decomposition
- **Memory**: Long-term and short-term memory systems
- **Vision**: Visual perception capabilities
- **Models**: Model management and inference

### Documentation (`/docs`)
- `architecture/` - System architecture diagrams and docs
- `agent/` - Agent design and implementation details
- `android/` - Android app documentation
- `development/` - Development guides and conventions

## Quick Start

### Prerequisites
- Java 17+ (for Android)
- Python 3.11+ (for AI core)
- Docker & Docker Compose (optional)

### Installation

```powershell
# Run installation script
.\scripts\install.ps1
```

### Building

```powershell
# Build everything
.\scripts\build.ps1

# Build only Android
.\scripts\build.ps1 -Android

# Build only AI
.\scripts\build.ps1 -AI

# Clean build
.\scripts\build.ps1 -Clean
```

### Running the AI Agent

```powershell
# Start the agent server
.\scripts\run-agent.ps1

# With auto-reload for development
.\scripts\run-agent.ps1 -Reload
```

### Using Docker

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## Development

### Code Style
- **Kotlin**: Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Python**: Black formatter, Ruff linter, MyPy type checking

### Testing
```powershell
# Android tests
cd android
./gradlew test

# AI tests
cd ai
pytest tests/ -v --cov
```

## Architecture

AURA follows a modular architecture:

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Android   │────▶│  AI Core    │────▶│  Services   │
│   Client    │     │  (Agent)    │     │  (Memory,   │
└─────────────┘     └─────────────┘     │   Vision)   │
                                        └─────────────┘
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests and linting
5. Submit a pull request

## License

MIT License - see LICENSE file for details.