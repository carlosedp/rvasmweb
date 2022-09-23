# RISC-V Assembler on Scala.js

An experimental RISC-V Assembly web app built on Scala.js.

The application is based on the [riscvassembler](https://github.com/carlosedp/riscvassembler) Scala library.

https://user-images.githubusercontent.com/20382/191991539-46dfa7c5-9965-4b98-9b57-b001eb3f492a.mov

## Development and Build

1. Install a JDK in your path (for Scala)
2. Install Node.js and npm
3. Install NPM dependencies with `npm install`
4. To start the development server, run `npm run start`, it will build the Scala.js Javascript files and run Vite dev server.
5. On another another shell, run `./mill -w rvasmweb.fastLinkJS` to constantly build the Scala.js files. Vite will pick the changes and reload the web app automatically.

Open <http://localhost:5173> for testing.

To bundle the application for production, run `npm run build`. The files will be placed on `./dist`.
