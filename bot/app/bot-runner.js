const { Telegraf } = require('telegraf')
const { Markup } = require('telegraf')
const CmdHandlers = require('./cmd-handlers')

class BotRunner{
  constructor(token) {
    this._bot = new Telegraf(token)
  }

  async _menuska(ctx){
    const keyboard = Markup.keyboard([
      ["Top"],
      ["Bottom1", "Bottom2"]
    ]);
    await _bot.telegram.sendMessage(ctx.chat.id, `something`, {
      reply_markup: {
        keyboard
      }
    })
  }

  initBot(){
    this._bot.start(CmdHandlers.startCmdHandler)
    this._bot.help((ctx) => ctx.reply('Send me a sticker'))
    this._bot.command("/self", CmdHandlers.selfCmdHandler)
    this._bot.on('sticker', (ctx) => ctx.reply('👍'))
    this._bot.command("/xlsxReport", (ctx) => CmdHandlers.xlsxReportCmdHandler(ctx))
    this._bot.hears('hi', this._menuska)
    this._bot.hears(':bar_chart: Статистика', (ctx) => ctx.reply('Hey there'))
    return this._bot;
  }
}

module.exports = {BotRunner}