import {defineConfig, UserConfig} from 'vite'

export default defineConfig(({command, mode}): UserConfig => {
    return {
        build: {
            target: "esnext"
        },
        server: {
            port: 80,
            watch: {
                usePolling: true,
            }
        }
    }
})
