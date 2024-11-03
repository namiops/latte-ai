# did_library

This is a a did (Decentralized Identifier), is a library to interacts, modify and create enteries in a DID Document in
accordance with [W3C DID Core Specification](https://www.w3.org/TR/did-core/), the underlying library being used is
DIDKit from spruceid and can be found on [DIDKit GitHub Repository](https://github.com/spruceid/didkit)

## Support

- ✅ Supported
- ❌ Not Supported
- ⚠️ Partially Supported
- 🚧 In Development
- 🧪 Mainly for Testing

| Target Platform | Target Architecture | Status |
|-----------------|---------------------|--------|
| Web (WASM32)    | N/A                 | 🚧     |
| Windows         | x86_64 (64-bit)     | ❌      |
| Linux           | x86_64 (64-bit)     | 🚧(🧪) |
| macOS           | x86_64 (64-bit)     | ❌      |
| iOS             | arm64 (ARM 64)      | ❌      |
| Android         | armv7, arm64        | ❌      |

## Expectations before any build

It is expected that you already have `rustup` installed together with `cargo` to start building the library for the
desired platform.

## Build Process Wasm (Flutter-Web)

For building the library for usage with eg flutter Web the easist way is to use `wasm-pack`

```bash
cargo install wasm-pack
```

this will install the packaging software for building wasm.
`wasm-pack` will also help you setup the initial linking with the final `.wasm` files.

### Building the wasm

```bash
wasm-pack build ./[path_to_library]/ --target web -d ../[path_to_flutter]/assets/scripts/
```

- **[path_to_library]:** this is the path to where the Cargo.toml exists
- **[path_to_flutter]:** this is the path to where the pubspec.yaml exists

> ***Note:***
>
>_The suggestion here is to put the contents in the `/assets/scripts` folder, as this can then be added to
the `pubspec.yaml::flutter.assets` section, this will allow flutters build process to add it to the application and
under web it will allow flutter to work as a PWA and autoembed the wasm files for the application to work offline_