const { Telegraf } = require('telegraf')
const { Markup } = require('telegraf')
const CmdHandlers = require('./cmd-handlers')

class BotRunner{
  constructor(token) {
    this._token= token;
    this._bot = new Telegraf(token);
  }

  async _menuska(ctx, bot){
    await bot.telegram.sendMessage(ctx.chat.id, `something`, {
      reply_markup: JSON.stringify({
        keyboard: [
          [{ text: "x1", }, {text: "x2"}],
          [{text:"x3"}]
        ]
      })
    })
  }

  initBot(){
    this._bot.start(CmdHandlers.startCmdHandler)
    this._bot.help((ctx) => ctx.reply('Send me a sticker'))
    this._bot.command("/self", CmdHandlers.selfCmdHandler)
    this._bot.on('sticker', (ctx) => ctx.reply('👍'))
    this._bot.command("/xlsxReport", (ctx) => CmdHandlers.xlsxReportCmdHandler(ctx))
    this._bot.hears('hi', ctx => this._menuska(ctx, this._bot))
    this._bot.hears(':bar_chart: Статистика', (ctx) => ctx.reply('Hey there'))
    return this._bot;
  }
}

module.exports = {BotRunner}