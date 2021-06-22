const axios = require('axios').default;
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
  '/xlsx' [s] - згенерувати xlsx репорт для сезону
  (s - опціонально, по дефолту - той, що на даний момент відкритий)
  --------------------------------------
  /subscribe - увімкнути сповіщення
  --------------------------------------
  /unsubscribe - вимкнути сповіщення
`
class CmdHandlers{
  constructor() {
    this._rc = new RabbitClient();
  }

  startCmdHandler(ctx){
    ctx.reply(startData)
  }

  selfCmdHandler(ctx){
    return ctx.reply(`
    Особисті дані:
    messageId = ${ctx.update.update_id}
    chatId = ${ctx.chat.id}
    userId = ${ctx.update.message.from.id}
    firstName = ${ctx.update.message.from.first_name ? ctx.update.message.from.first_name : 'n/a'}
    lastName = ${ctx.update.message.from.last_name ? ctx.update.message.from.last_name : 'n/a'}
    `);
  }

  async listPlayersCmdHandler(ctx){
    await this._rc.publish(this._dtoIn("listPlayers", ctx));
  }

  async statsCmdHandler(ctx, isCmd = true){
    let season;
    if (isCmd){
      const cmdWithArgs = ctx.message.text.trim().split(" ");
      cmdWithArgs.shift();
      season = cmdWithArgs.length > 0 ? cmdWithArgs[0] : this._currentQuarter();
    }else{
      season = this._currentQuarter();
    }
    await this._rc.publish(this._dtoIn("shortStats", ctx, {season}));
  }

  async lastGamesCmdHandler(ctx, isCmd = true){
    let data = {
      season: this._currentQuarter(),
      qty: 6
    };
    if (isCmd){
      const cmdWithArgs = ctx.message.text.trim().split(" ");
      cmdWithArgs.shift();
      if (cmdWithArgs.length === 1){
        data.season = cmdWithArgs[0];
      }else if (cmdWithArgs.length ===2){
        data.season = cmdWithArgs[0];
        data.qty = cmdWithArgs[1];
      }
    }
    await this._rc.publish(this._dtoIn("findLastRounds", ctx, data));
  }

  async xlsxReportCmdHandler(ctx, isCmd = true){
    let season = this._currentQuarter();
    if (isCmd){
      const cmdWithArgs = ctx.message.text.trim().split(" ");
      cmdWithArgs.shift();
      if (cmdWithArgs.length === 1){
        season = cmdWithArgs[0];
      }
    }
    const doc = await this.loadFile(`http://localhost:9091/reports/xlsx/${season}`, 'GET')
    ctx.replyWithDocument({source: doc, filename: `${season.replace("|","_")}.xlsx`})
  }

  // async statsCmdHandler(ctx){
  //   const x = await this.loadFile()
  //   ctx.replyWithDocument({source: x, filename: "test.xlsx"})
  // }

  async loadFile(uri, method){
    try {
      const response = await axios({
        url: uri,
        method: method,
        responseType: 'stream'
      });
      return response.data;
    } catch (error) {
      throw  error;
    }
  }

  _currentQuarter(){
    const now = new Date();
    const month = now.getMonth()+1;
    let quarter;
    if ([1,2,3].includes(month)){
      quarter = 1;
    }else if ([4,5,6].includes(month)){
      quarter = 2;
    }else if([7,8,9].includes(month)){
      quarter = 3;
    }else {
      quarter = 4;
    }
    return `S${quarter}|${now.getFullYear()}`
  }

  _dtoIn(eventCode, ctx, data = {}){
    return {
      cmd: eventCode,
      chatId: ctx.chat.id,
      tid: ctx.message.from.id,
      user: ctx.message.from.first_name,
      data
    }
  }

}

module.exports = new CmdHandlers();