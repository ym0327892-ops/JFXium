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
  { input: 'theme-dark.less', output: 'theme-dark.css' },
  { input: 'theme-light-compact.less', output: 'theme-light-compact.css' },
  { input: 'theme-dark-compact.less', output: 'theme-dark-compact.css' },
  { input: 'theme-mui.less', output: 'theme-mui.css' },
  { input: 'theme-mui-dark.less', output: 'theme-mui-dark.css' },
  { input: 'theme-mui-compact.less', output: 'theme-mui-compact.css' },
  { input: 'theme-mui-dark-compact.less', output: 'theme-mui-dark-compact.css' },
  { input: 'theme-cyberpunk.less', output: 'theme-cyberpunk.css' },
  { input: 'theme-shadcn.less', output: 'theme-shadcn.css' },
  { input: 'theme-custom.less', output: 'theme-custom.css' }
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
