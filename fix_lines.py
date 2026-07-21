with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'r') as f:
    lines = f.readlines()

insert_indices = [251, 285, 290, 306, 431, 438, 483, 489, 518, 525, 532, 539]
# Note: since we insert, indices will shift. We process in reverse order.
for idx in sorted(insert_indices, reverse=True):
    # idx is 1-based, so array index is idx-1
    lines.insert(idx - 1, "            )\n")

with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'w') as f:
    f.writelines(lines)
