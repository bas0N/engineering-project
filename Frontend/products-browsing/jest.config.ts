import type { JestConfigWithTsJest } from 'ts-jest'

const config: JestConfigWithTsJest = {
  extensionsToTreatAsEsm: ['.ts'],
  verbose: true,
  preset: 'ts-jest/presets/default-esm',
  testEnvironment: 'node',
  transform: {
    '^.+\\.tsx?$': ['ts-jest', { 
      useESM: true,
      diagnostics: {
        ignoreCodes: [1343],
      },
      astTransformers: {
        before: [
          {
            path: 'node_modules/ts-jest-mock-import-meta',
            options: {
              metaObjectReplacement: {
                env: {
                  VITE_PREVIEW_MODE: true,
                  VITE_API_URL: 'http://localhost:3001/',
                },
              },
            },
          },
        ],
      },
    }]
  },
  testPathIgnorePatterns: ['./dist']
}

export default config