#!/usr/bin/env node
// ============================================
// JFXium LESS Theme Builder
// Usage: node build-themes.js
// Requires: npm install -g less
// ============================================

const { execSync } = require('child_process');
const path = require('path');

const LESS_DIR = path.resolve(__dirname);
const CSS_DIR = path.resolve(LESS_DIR, '..');

const themes = [
  { input: 'theme-light.less', output: 'theme-light.css' },
  { input: 'theme-dark.less', output: 'theme-dark.css' }
];

function compileTheme(inputFile, outputFile) {
  const inputPath = path.join(LESS_DIR, inputFile);
  const outputPath = path.join(CSS_DIR, outputFile);

  console.log(`Compiling ${inputFile}...`);

  try {
    execSync(`npx lessc "${inputPath}" "${outputPath}"`, {
      stdio: 'pipe',
      cwd: path.resolve(__dirname, '../../../../../..')
    });
    console.log(`  ✓ Generated ${outputFile}`);
  } catch (err) {
    console.error(`  ✗ Failed to compile ${inputFile}:`);
    console.error(`    ${err.stderr?.toString() || err.message}`);
    process.exit(1);
  }
}

function main() {
  console.log('============================================');
  console.log('JFXium Theme Builder');
  console.log('============================================');

  for (const theme of themes) {
    compileTheme(theme.input, theme.output);
  }

  console.log('============================================');
  console.log('All themes compiled successfully!');
  console.log('============================================');
}

main();
