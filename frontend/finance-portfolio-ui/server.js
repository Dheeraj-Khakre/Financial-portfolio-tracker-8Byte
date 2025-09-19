// server.js  – for serving a plain Angular SPA build
const express = require('express');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 8080;

// Serve static files from the Angular dist folder
app.use(express.static(path.join(__dirname, 'dist', 'finance-portfolio-ui', 'browser')));

// All other routes -> index.html so Angular can handle client-side routing
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist', 'finance-portfolio-ui', 'browser', 'index.html'));
});

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
