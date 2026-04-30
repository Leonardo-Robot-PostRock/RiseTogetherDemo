#!/bin/bash
# ITJobsBackend JDK Setup Script
# Usage: ./setup-jdk.sh [version]
# Default: JDK 25

set -e

VERSION="${1:-25}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=== ITJobsBackend JDK Setup ==="
echo "Target JDK: $VERSION"
echo ""

# Find available Java
find_java() {
  # macOS locations
  for v in 25 21; do
    for prefix in temurin openjdk; do
      if [ -d "/Library/Java/JavaVirtualMachines/${prefix}-${v}.jdk" ]; then
        echo "/Library/Java/JavaVirtualMachines/${prefix}-${v}.jdk/Contents/Home"
        return 0
      fi
    done
  done

  # Homebrew
  for v in 25 21; do
    if [ -d "/opt/homebrew/Cellar/openjdk@${v}" ]; then
      echo "/opt/homebrew/Cellar/openjdk@${v}/${v}.0.11/libexec/openjdk.jdk/Contents/Home"
      return 0
    fi
  done

  return 1
}

# Install JDK using Homebrew
install_jdk() {
  local v="$1"
  
  echo "Installing OpenJDK $v..."

  if ! command -v brew >/dev/null 2>&1; then
    echo "ERROR: Homebrew is not installed."
    echo "Install Homebrew first:"
    echo "  /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
    exit 1
  fi

  # Try different package names
  local packages=(
    "temurin@${v}"
    "openjdk@${v}"
    "openjdk"
  )

  for pkg in "${packages[@]}"; do
    if brew install --cask "$pkg" 2>/dev/null; then
      echo "✓ Installed $pkg"
      return 0
    elif brew install "$pkg" 2>/dev/null; then
      echo "✓ Installed $pkg"
      return 0
    fi
  done

  echo "ERROR: Could not install JDK automatically."
  echo "Please install manually and re-run this script."
  exit 1
}

# Check if Java is available
JAVA_HOME=$(find_java) || {
  echo "JDK $VERSION not found."
  echo "Attempting to install..."
  install_jdk "$VERSION"
  JAVA_HOME=$(find_java) || {
    echo "ERROR: Could not find or install JDK."
    exit 1
  }
}

echo "✓ Using JDK: $JAVA_HOME"
echo ""

# Verify Java version
"$JAVA_HOME/bin/java" -version 2>&1 | head -1

echo ""
echo "=== Setup Complete ==="
echo "JAVA_HOME=$JAVA_HOME"
echo ""
echo "To use this JDK in your current shell:"
echo "  export JAVA_HOME=$JAVA_HOME"
echo ""
echo "To make it permanent, add to your ~/.zshrc:"
echo "  echo 'export JAVA_HOME=$JAVA_HOME' >> ~/.zshrc"
echo ""
echo "To run tests:"
echo "  ./mvnw test"