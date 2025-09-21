
const express = require('express');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 4200;

app.use(express.static(path.join(__dirname, 'dist', 'finance-portfolio-ui', 'browser')));

app.get(/.*/, (req, res) => {
  res.sendFile(path.join(__dirname, 'dist', 'finance-portfolio-ui', 'browser', 'index.csr.html'));
});

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
//// Listen on 0.0.0.0 for Docker networking
//app.listen(PORT, '0.0.0.0', () => {
//  console.log(`Server running on http://0.0.0.0:${PORT}`);
//});
