const {BotRunner} = require("./app/bot-runner")
const dotenv = require('dotenv').config();

const br = new BotRunner("")
br.initBot().launch()
process.once('SIGINT', () => bot.stop('SIGINT'))
process.once('SIGTERM', () => bot.stop('SIGTERM'))