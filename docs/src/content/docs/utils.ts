export function extractCodePart(source: string, marker: string): string {
    if (typeof source !== "string") {
        return "Invalid source.";
    }

    var result = [];
    var addFlag = false;
    var trimSpaces: number | undefined = undefined;
    const regex = new RegExp(`@part:${marker}`);
    for (var line of source.split('\n')) {
        if (regex.test(line)) {
            addFlag = !addFlag;
        } else if (addFlag) {
            if (/@part:[a-zA-Z0-9_-]/.test(line)) {
                continue;
            }
            const leadingSpaces = line.search(/\S/);
            if (leadingSpaces >= 0) {
                if (trimSpaces == undefined) {
                    trimSpaces = leadingSpaces
                } else if (leadingSpaces < trimSpaces) {
                    trimSpaces = leadingSpaces
                }
            }
            result.push(line)
        }
    }
    return result.length == 0
        ? "Not found: @part:" + marker
        : result.map(s => s.substring(trimSpaces ?? 0)).join('\n');
}