const axios = require('axios').default
const { RabbitClient } = require('./rabbit-client')

const startData = `
  /start - інфа про можливості бота
  --------------------------------------
  /self - Дані про себе
  --------------------------------------
  /players - Всі існуючі юзери з id
  --------------------------------------
  /last [s][n] - показати n останніх матчів
  (s - опціонально, по дефолту current season)
  (n - опціонально, по дефолту 6)
  --------------------------------------
  /stats [x] - рейтинг гравців у сезоні
  (x - формат сезона, опціонально)
  якщо x відсутній, то now()
  приклад: S1|2019, S4|2020
  --------------------------------------
  /xlsx [s] - згенерувати xlsx репорт для сезону
  (s - опціонально, по дефолту - той, що на даний момент відкритий)
  --------------------------------------
  /linkTid [chatId surname] - увімкнути сповіщення юзеру
  (тільки для адмінів)
  --------------------------------------
  /subscribe - увімкнути сповіщення
  --------------------------------------
  /unsubscribe - вимкнути сповіщення
`
class CmdHandlers {
  constructor () {
    this._rc = new RabbitClient()
  }

  startCmdHandler (ctx) {
    ctx.reply(startData)
  }

  selfCmdHandler (ctx) {
    return ctx.reply(`
    Особисті дані:
    messageId = ${ctx.update.update_id}
    chatId = ${ctx.chat.id}
    userId = ${ctx.update.message.from.id}
    firstName = ${ctx.update.message.from.first_name ? ctx.update.message.from.first_name : 'n/a'}
    lastName = ${ctx.update.message.from.last_name ? ctx.update.message.from.last_name : 'n/a'}
    `)
  }

  async listPlayersCmdHandler (ctx) {
    await this._rc.publish(this._dtoIn('listPlayers', ctx))
  }

  async statsCmdHandler (ctx, isCmd = true) {
    let season
    if (isCmd) {
      const args = this._parseArgs(ctx.message.text)
      season = args.length > 0 ? args[0] : this._currentQuarter()
    } else {
      season = this._currentQuarter()
    }
    await this._rc.publish(this._dtoIn('shortStats', ctx, { season }))
  }

  async lastGamesCmdHandler (ctx, isCmd = true) {
    const data = {
      season: this._currentQuarter(),
      qty: 6
    }
    if (isCmd) {
      const args = this._parseArgs(ctx.message.text)
      if (args.length === 1) {
        data.season = args[0]
      } else if (args.length === 2) {
        data.season = args[0]
        data.qty = args[1]
      }
    }
    await this._rc.publish(this._dtoIn('findLastRounds', ctx, data))
  }

  async xlsxReportCmdHandler (ctx, isCmd = true) {
    let season = this._currentQuarter()
    if (isCmd) {
      const args = this._parseArgs(ctx.message.text)
      if (args.length === 1) {
        season = args[0]
      }
    }
    const doc = await this._loadFile(`${process.env.ARBITER_URI}/reports/xlsx/${season}`, 'GET')
    await ctx.replyWithDocument({ source: doc, filename: `${season.replace('|', '_')}.xlsx` })
  }

  async loadDumpCmdHandler (ctx) {
    const now = new Date().toDateString()
    const date = now.split(' ').join('_')
    const doc = await this._loadFile(`${process.env.ARBITER_URI}/dump/export/${ctx.message.from.id}`, 'GET')
    await ctx.replyWithDocument({ source: doc, filename: `dump_${date}.zip` })
  }

  async subscribeCmdHandler (ctx, subscribe) {
    const cmd = subscribe ? 'subscribe' : 'unsubscribe'
    const data = {
      enableSubscriptions: subscribe,
      tid: ctx.message.from.id
    }
    await this._rc.publish(this._dtoIn(cmd, ctx, data))
  }

  async linkTidCmdHandler (ctx) {
    const args = this._parseArgs(ctx.message.text)
    const [chatId, name] = args
    const data = {
      tid: chatId,
      nameToLink: name,
      moderator: ctx.message.from.id
    }
    await this._rc.publish(this._dtoIn('linkTid', ctx, data))
  }

  async activateCmdHandler (ctx, isActivated) {
    const cmd = isActivated ? 'activate' : 'deactivate'
    const args = this._parseArgs(ctx.message.text)
    const data = {
      players: args,
      moderator: ctx.message.from.id
    }
    await this._rc.publish(this._dtoIn(cmd, ctx, data))
  }

  async addRoundCmdHandler (ctx) {
    const args = this._parseArgs(ctx.message.text)
    const data = {
      shutout: args.includes('суха'),
      moderator: ctx.message.from.id
    }
    const pairs = args.filter(x => x.indexOf('/') > 0)
    if (pairs.length === 2) {
      const [w1, w2] = pairs[0].split('/')
      const [l1, l2] = pairs[1].split('/')
      data.w1 = w1
      data.w2 = w2
      data.l1 = l1
      data.l2 = l2
    }
    await this._rc.publish(this._dtoIn('addRound', ctx, data))
  }

  async addPlayerCmdHandler (ctx) {
    const args = this._parseArgs(ctx.message.text)
    const data = {
      admin: args.includes('адмін'),
      moderator: ctx.message.from.id
    }
    if (args.length >= 2) {
      data.surname = args[0]
      data.tid = args[1]
    } else if (args.length < 2) {
      data.surname = args[0]
    }
    await this._rc.publish(this._dtoIn('addPlayer', ctx, data))
  }

  async broadcastMessage (ctx) {
    const args = this._parseArgs(ctx.message.text)
    const data = {
      author: ctx.message.from.id,
      message: args[0]
    }
    await this._rc.publish(this._dtoIn('broadcastMessage'), ctx, data)
  }

  async _loadFile (uri, method) {
    const response = await axios({
      url: uri,
      method: method,
      responseType: 'stream'
    })
    return response.data
  }

  _parseArgs (text) {
    const args = text.trim().split(' ')
    args.shift()
    return args
  }

  _currentQuarter () {
    const now = new Date()
    const month = now.getMonth() + 1
    let quarter
    if ([1, 2, 3].includes(month)) {
      quarter = 1
    } else if ([4, 5, 6].includes(month)) {
      quarter = 2
    } else if ([7, 8, 9].includes(month)) {
      quarter = 3
    } else {
      quarter = 4
    }
    return `S${quarter}|${now.getFullYear()}`
  }

  _dtoIn (eventCode, ctx, data = {}) {
    return {
      cmd: eventCode,
      chatId: ctx.chat.id,
      tid: ctx.message.from.id,
      user: ctx.message.from.first_name,
      data
    }
  }
}

module.exports = new CmdHandlers()
