# cuid2 [![CircleCI](https://dl.circleci.com/status-badge/img/gh/hden/cuid2/tree/main.svg?style=svg&circle-token=e446bb4839884cea2d33a789e43f57bdc0823200)](https://dl.circleci.com/status-badge/redirect/gh/hden/cuid2/tree/main)
[![Clojars Project](https://img.shields.io/clojars/v/com.github.hden/cuid2.svg)](https://clojars.org/com.github.hden/cuid2)

Secure, collision-resistant ids optimized for horizontal scaling and performance. Next generation UUIDs.

Ported from https://github.com/paralleldrive/cuid2

## Usage

```clj
(require '[cuid2.core :refer [cuid cuid?]])

(cuid)
; "j9f203xe1wxw8nkor3yd4bkp"

(cuid? (cuid))
; true

(cuid {:length 32})
; "hjm1wxqkahfzh58h936ryr5aqh70iwe9"
```

## Testing

See test/cuid2/core_test.clj

## License

Copyright © 2023 Haokang Den

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
