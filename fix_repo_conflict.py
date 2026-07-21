import re

with open('/app/applet/app/src/main/java/com/example/data/Repositories.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace("interface UsageRepository {", "interface IUsageRepository {")

with open('/app/applet/app/src/main/java/com/example/data/Repositories.kt', 'w', encoding='utf-8') as f:
    f.write(content)

