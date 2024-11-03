
# OpenSSL related configurations 

## configuration for most applications built with OpenSSL 3.x

* An environment variable `OPENSSL_CONF` shall be set to load custom openssl configuration file to enable pkcs11-provider and pkcs11_iotsafe.

* See `openssl_iotsafe.cnf` , `71-sign_verify_ossl3.sh` and `mtls_iotsafe_client.sh` in this directory for working example.

* Instead default configuration file (/etc/ssl/openssl.cnf) can be modified to globally enable support for IoT security applet without `OPENSSL_CONF` environment variable.

* See [pkcs11-provider documentation](https://github.com/latchset/pkcs11-provider/) for more details about the configuration file syntax.

## configuration for legacy applications still using OpenSSL 1.x ENGINE API

* Please install engine_pkcs11 first to enable support for ENGINE API.

  A command `sudo apt install libengine-pkcs11-openssl` will install it on debian-based systems. 

* An environment variable `PKCS11_MODULE_PATH` shall be set so that engine_pkcs11 loads libpkcs11_iotsafe.so to enable support for IoT security applet.

    ```bash
    export PKCS11_MODULE_PATH=/usr/lib/x86_64-linux-gnu/libpkcs11_iotsafe.so
    ```

* Additional command line options are also needed to enable engine_pkcs11. It will vary for each software. 
    * For example, `-engine pkcs11 --keyform ENGINE` options required for `openssl dgst`.
    * curl requires `--engine pkcs11 ` option.

* Please see `72-sign_verify_ossl_engine` and `mtls_curl.sh` for working examples of above configurations.

* Please note that the ENGINE API is deprecated in OpenSSL 3.0. It is not recommended for new designs.


