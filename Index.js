const apiKey = process.env.OPENROUTER_API_KEY;

fetch('https://openrouter.ai/api/v1/chat/completions', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${apiKey}`,
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    model: 'openai/gpt-4o',
    messages: [{ role: 'user', content: 'Hello!' }],
  }),
})
.then(res => res.json())
.then(data => console.log(data.choices[0].message.content));
