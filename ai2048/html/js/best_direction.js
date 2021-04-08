// function BestDirection(){
//   this.listen()
// }
window.flag = true
function getBestMove(cellInfo){
  var operation
  window.flag = false;
  $.ajax({
    url:"http://localhost:18090/v1/ai/2048/getBestMove",
    type:"POST",
    traditional: true,
    async:false,
    data:{
      "cellInfo" : cellInfo
    },
    dataType:"json",
    success:function(data) {
      operation = data;
    },
    error:function() {
      alert("服务器响应失败。。。");
    }
  })
  console.log("operation -----> " + operation);
  window.flag = false;
  return operation;
}

function getCellInfo(){
  var cellInfo = new Array();
  for (var i = 1; i <= 4; i++) {
    for(var j = 1; j <= 4; j++) {
      var tileClass = '.tile-position-' + j + '-' + i + ':last ' + '.tile-inner';
      var tile = $(tileClass).text();
      if (tile == '') {
        tile = 0;
      }
      cellInfo.push(Number(tile));
    }
  }
  return cellInfo;
}

BestDirection.prototype.emit = function (event, data) {
  var callbacks = this.events[event];
  if (callbacks) {
    callbacks.forEach(function (callback) {
      callback(data);
    });
  }
};

function BestDirection() {
  this.events = {};

  if (window.navigator.msPointerEnabled) {
    //Internet Explorer 10 style
    this.eventTouchstart    = "MSPointerDown";
    this.eventTouchmove     = "MSPointerMove";
    this.eventTouchend      = "MSPointerUp";
  } else {
    this.eventTouchstart    = "touchstart";
    this.eventTouchmove     = "touchmove";
    this.eventTouchend      = "touchend";
  }

  this.listen();
}

BestDirection.prototype.on = function (event, callback) {
  if (!this.events[event]) {
    this.events[event] = [];
  }
  this.events[event].push(callback);
};

BestDirection.prototype.listen = function () {
  var self = this;

  var map = {
    38: 0, // Up
    39: 1, // Right
    40: 2, // Down
    37: 3, // Left
    75: 0, // Vim up
    76: 1, // Vim right
    74: 2, // Vim down
    72: 3, // Vim left
    87: 0, // W
    68: 1, // D
    83: 2, // S
    65: 3  // A
  };

  // Respond to direction keys
  document.addEventListener("load", function (event) {
    var modifiers = event.altKey || event.ctrlKey || event.metaKey ||
                  event.shiftKey;
    var mapped    = map[event.which];
   

    // R key restarts the game
    if (!modifiers && event.which === 82) {
      self.restart.call(self, event);
    }
      
  });

  var count = 0;
  var timer = setInterval(function(){
    count++;     
    if(window.flag || count >= 5) {  
      count = 0;
      window.flag = false;
      self.emit("move", getBestMove(getCellInfo()));
    }
  }, 250)

  // Respond to button presses
  this.bindButtonPress(".retry-button", this.restart);
  this.bindButtonPress(".restart-button", this.restart);
  this.bindButtonPress(".keep-playing-button", this.keepPlaying);

  // Respond to swipe events
  var touchStartClientX, touchStartClientY;
  var gameContainer = document.getElementsByClassName("game-container")[0];

  gameContainer.addEventListener(this.eventTouchstart, function (event) {
    if ((!window.navigator.msPointerEnabled && event.touches.length > 1) ||
        event.targetTouches.length > 1) {
      return; // Ignore if touching with more than 1 finger
    }

    if (window.navigator.msPointerEnabled) {
      touchStartClientX = event.pageX;
      touchStartClientY = event.pageY;
    } else {
      touchStartClientX = event.touches[0].clientX;
      touchStartClientY = event.touches[0].clientY;
    }

    event.preventDefault();
  });

  gameContainer.addEventListener(this.eventTouchmove, function (event) {
    event.preventDefault();
  });

  gameContainer.addEventListener(this.eventTouchend, function (event) {
    if ((!window.navigator.msPointerEnabled && event.touches.length > 0) ||
        event.targetTouches.length > 0) {
      return; // Ignore if still touching with one or more fingers
    }
    
    self.emit("move", getBestMove(getCellInfo()));
  });
};

BestDirection.prototype.restart = function (event) {
  event.preventDefault();
  this.emit("restart");
};

BestDirection.prototype.keepPlaying = function (event) {
  event.preventDefault();
  this.emit("keepPlaying");
};

BestDirection.prototype.bindButtonPress = function (selector, fn) {
  var button = document.querySelector(selector);
  button.addEventListener("click", fn.bind(this));
  button.addEventListener(this.eventTouchend, fn.bind(this));
};

BestDirection.prototype.switchFlag = function() {
  var nowFlag = flag;
  flag = !nowFlag
}
