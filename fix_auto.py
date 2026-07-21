import re

with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
for i in range(len(lines)):
    new_lines.append(lines[i])
    if lines[i].strip() in ["}", "})", "}"]:
        continue
        
    match = re.match(r'^(\s+)(Text|Spacer|Icon|QuickActionButton)\($', lines[i])
    if match:
        indent = match.group(1)
        # Scan ahead to see where it closes
        closed = False
        for j in range(i+1, min(i+20, len(lines))):
            if lines[j].startswith(indent + ")"):
                closed = True
                break
            if lines[j].startswith(indent + "}") or (len(lines[j].strip()) > 0 and not lines[j].startswith(indent + "    ") and not lines[j].startswith(indent + " ")):
                break
        
        if not closed:
            # We need to insert a `indent + )` at the end of the arguments.
            # Find the last line that is indented more than the call.
            insert_idx = i + 1
            while insert_idx < len(lines) and (lines[insert_idx].startswith(indent + " ") or lines[insert_idx].strip() == ""):
                insert_idx += 1
            
            lines.insert(insert_idx, indent + ")\n")

with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'w') as f:
    f.writelines(lines)
