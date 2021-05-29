const {BotRunner} = require("./app/bot-runner")
const dotenv = require('dotenv').config();

const br = new BotRunner("1721059759:AAEyozK5-R3-7EN4yNz2jo-QvpOMoIF-iBI")
br.initBot().launch()
process.once('SIGINT', () => bot.stop('SIGINT'))
process.once('SIGTERM', () => bot.stop('SIGTERM'))