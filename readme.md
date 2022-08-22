### 整理的一些面试题

[传送门 - 小橙子在北京](https://github.com/qugemingzizhemefeijin)

[传送门 - YephetsSkys](https://github.com/YephetsSkys)

### openjdk-8 -- 使用 Clion 调试源码

[openjdk-8 -- 使用 Clion 调试源码](https://rqsir.github.io/2019/04/19/openjdk-8-%E4%BD%BF%E7%94%A8Clion%E8%B0%83%E8%AF%95%E6%BA%90%E7%A0%81/)

对于可能遇到的头文件不包含问题，解决如下：

clion 导入源码之后遇到头文件找不到的问题，而实际上这些头文件在源码里面是存在的，只不过在某些源文件里面是以相对路径的方式来搜索，可以在 CMakeLists.txt 里面添加一些根路径

```
include_directories(./src/share/vm)
include_directories(./src/cpu/x86/vm)
include_directories(./src/share/vm/precompiled)
include_directories(./src/share/vm/utilities)
```

另外，如果某些头文件依然找不到，可以手工导入，然后把导入的头文件加到
`hotspot/src/share/vm/precompiled/precompiled.hpp`里，因为大多数源文件都会包含这个源文件

```
# include <cstdlib>
# include <cstdint>
# include "register_x86.hpp"
# include "assembler_x86.hpp"
# include "globalDefinitions.hpp"
# include "globalDefinitions_x86.hpp"
# include "assembler_x86.hpp"
#include <stubRoutines_x86.hpp>
```
