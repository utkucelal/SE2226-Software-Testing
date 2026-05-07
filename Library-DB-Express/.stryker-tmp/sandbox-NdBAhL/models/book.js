// @ts-nocheck
'use strict';

function stryNS_9fa48() {
  var g = typeof globalThis === 'object' && globalThis && globalThis.Math === Math && globalThis || new Function("return this")();
  var ns = g.__stryker__ || (g.__stryker__ = {});
  if (ns.activeMutant === undefined && g.process && g.process.env && g.process.env.__STRYKER_ACTIVE_MUTANT__) {
    ns.activeMutant = g.process.env.__STRYKER_ACTIVE_MUTANT__;
  }
  function retrieveNS() {
    return ns;
  }
  stryNS_9fa48 = retrieveNS;
  return retrieveNS();
}
stryNS_9fa48();
function stryCov_9fa48() {
  var ns = stryNS_9fa48();
  var cov = ns.mutantCoverage || (ns.mutantCoverage = {
    static: {},
    perTest: {}
  });
  function cover() {
    var c = cov.static;
    if (ns.currentTestId) {
      c = cov.perTest[ns.currentTestId] = cov.perTest[ns.currentTestId] || {};
    }
    var a = arguments;
    for (var i = 0; i < a.length; i++) {
      c[a[i]] = (c[a[i]] || 0) + 1;
    }
  }
  stryCov_9fa48 = cover;
  cover.apply(null, arguments);
}
function stryMutAct_9fa48(id) {
  var ns = stryNS_9fa48();
  function isActive(id) {
    if (ns.activeMutant === id) {
      if (ns.hitCount !== void 0 && ++ns.hitCount > ns.hitLimit) {
        throw new Error('Stryker: Hit count limit reached (' + ns.hitCount + ')');
      }
      return true;
    }
    return false;
  }
  stryMutAct_9fa48 = isActive;
  return isActive(id);
}
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  if (stryMutAct_9fa48("0")) {
    {}
  } else {
    stryCov_9fa48("0");
    class Book extends Model {
      static associate(models) {}
    }
    ;
    Book.init(stryMutAct_9fa48("1") ? {} : (stryCov_9fa48("1"), {
      title: stryMutAct_9fa48("2") ? {} : (stryCov_9fa48("2"), {
        type: DataTypes.STRING,
        allowNull: stryMutAct_9fa48("3") ? true : (stryCov_9fa48("3"), false),
        validate: stryMutAct_9fa48("4") ? {} : (stryCov_9fa48("4"), {
          notEmpty: stryMutAct_9fa48("5") ? {} : (stryCov_9fa48("5"), {
            msg: stryMutAct_9fa48("6") ? "" : (stryCov_9fa48("6"), "Please Provide a Value For Title")
          })
        })
      }),
      author: stryMutAct_9fa48("7") ? {} : (stryCov_9fa48("7"), {
        type: DataTypes.STRING,
        allowNull: stryMutAct_9fa48("8") ? true : (stryCov_9fa48("8"), false),
        validate: stryMutAct_9fa48("9") ? {} : (stryCov_9fa48("9"), {
          notEmpty: stryMutAct_9fa48("10") ? {} : (stryCov_9fa48("10"), {
            msg: stryMutAct_9fa48("11") ? "" : (stryCov_9fa48("11"), "Please Provide a Value For Author")
          })
        })
      }),
      genre: DataTypes.STRING,
      year: DataTypes.INTEGER
    }), stryMutAct_9fa48("12") ? {} : (stryCov_9fa48("12"), {
      sequelize,
      modelName: stryMutAct_9fa48("13") ? "" : (stryCov_9fa48("13"), 'Book')
    }));
    return Book;
  }
};