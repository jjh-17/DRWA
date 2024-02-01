/* eslint-env node */
require('@rushstack/eslint-patch/modern-module-resolution')

module.exports = {
  // 현재 eslintrc 파일을 기준으로 ESLint 규칙을 적용
  root: true,
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    '@vue/eslint-config-prettier/skip-formatting'
  ],
  // 코드 정리 플러그인 추가
  plugins: ['prettier'],
  // 추가적인 규칙들을 적용
  env: {
    node: true,
    'vue/setup-compiler-macros': true
  },
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  // 사용자 편의 규칙 추가
  rules: {
    'prettier/prettier': [
      'error',
      // 아래 규칙들은 개인 선호에 따라 prettier 문법 적용
      // https://prettier.io/docs/en/options.html
      {
        singleQuote: true,
        semi: false,
        tabWidth: 2,
        trailingComma: 'none',
        printWidth: 100,
        bracketSpacing: true,
        arrowParens: 'avoid',
        endOfLine: 'auto'
      }
    ]
  }
}
