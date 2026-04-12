import json
import os
import sys
from pathlib import Path

from openai import OpenAI

def read_text(path: str) -> str:
    return Path(path).read_text(encoding="utf-8")

def main():
    if len(sys.argv) != 3:
        print("Usage: python ai_review.py <spec_path> <diff_path>", file=sys.stderr)
        sys.exit(1)

    spec_path = sys.argv[1]
    diff_path = sys.argv[2]

    spec_text = read_text(spec_path)
    diff_text = read_text(diff_path)

    client = OpenAI(api_key=os.environ["OPENAI_API_KEY"])

    prompt = f"""
You are a senior reviewer doing preliminary SDD review.

Task:
Compare the PR diff against the spec.

Return markdown with these sections only:
1. Summary
2. Spec alignment
3. Missing tests
4. Contract / API concerns
5. Risky changes outside scope
6. Reviewer focus

Rules:
- Be concrete and brief
- Only mention issues supported by the spec or diff
- If something looks fine, say so
- Do not invent files or code not present

=== SPEC ===
{spec_text}

=== DIFF ===
{diff_text}
"""

    response = client.responses.create(
        model="gpt-5.4",
        input=prompt,
    )

    output = response.output_text.strip()
    print(output)

if __name__ == "__main__":
    main()