const { Telegraf } = require('telegraf')
const CmdHandlers = require('./cmd-handlers')
const { RabbitClient } = require('./rabbit-client')

class BotRunner {
  constructor (token) {
    this._bot = new Telegraf(token)
    this._rc = new RabbitClient()
  }

  async initBot () {
    this._bot.start(CmdHandlers.startCmdHandler)
    this._bot.help((ctx) => ctx.reply('Send me a sticker'))

    this._bot.on('sticker', (ctx) => ctx.reply('👍'))

    this._bot.command('/self', CmdHandlers.selfCmdHandler)
    this._bot.command('/stats', (ctx) => CmdHandlers.statsCmdHandler(ctx))
    this._bot.command('/players', (ctx) => CmdHandlers.listPlayersCmdHandler(ctx))
    this._bot.command('/last', (ctx) => CmdHandlers.lastGamesCmdHandler(ctx))
    this._bot.command('/xlsxReport', (ctx) => CmdHandlers.xlsxReportCmdHandler(ctx))
    this._bot.command('/subscribe', (ctx) => CmdHandlers.subscribeCmdHandler(ctx, true))
    this._bot.command('/unsubscribe', (ctx) => CmdHandlers.subscribeCmdHandler(ctx, false))
    this._bot.command('/linkTid', (ctx) => CmdHandlers.linkTidCmdHandler(ctx, false))
    this._bot.command('/activate', (ctx) => CmdHandlers.activateCmdHandler(ctx, true))
    this._bot.command('/deactivate', (ctx) => CmdHandlers.activateCmdHandler(ctx, false))
    this._bot.command('/add', (ctx) => CmdHandlers.addRoundCmdHandler(ctx))
    this._bot.command('/register', (ctx) => CmdHandlers.addPlayerCmdHandler(ctx))
    this._bot.command('/dump', (ctx) => CmdHandlers.loadDumpCmdHandler(ctx))

    this._bot.hears('Cтатистика \uD83D\uDCC8', (ctx) => CmdHandlers.statsCmdHandler(ctx, false))
    this._bot.hears('Всі гравці \uD83D\uDDFF', (ctx) => CmdHandlers.listPlayersCmdHandler(ctx))
    this._bot.hears('Останні партії \uD83D\uDCCB', (ctx) => CmdHandlers.lastGamesCmdHandler(ctx, false))
    this._bot.hears('Завантажити .xlsx \uD83D\uDCBE', (ctx) => CmdHandlers.xlsxReportCmdHandler(ctx, false))
    await this._rc.initConsumer(this._bot)
    return this._bot
  }
}

module.exports = { BotRunner }
