const { BotRunner } = require('./app/bot-runner')
const dotenv = require('dotenv')

console.log('Starting bot...')
dotenv.config()
const br = new BotRunner(process.env.TOKEN)
br.initBot().then(bot => {
  process.once('SIGTERM', () => bot.stop('SIGTERM'))
  process.once('SIGINT', () => bot.stop('SIGINT'))
  console.log('Bot was successfully started')
  return bot.launch()
}).catch(err => {
  console.error(err)
  process.exit(1)
  console.log('Bot has crashed unexpectedly')
})
