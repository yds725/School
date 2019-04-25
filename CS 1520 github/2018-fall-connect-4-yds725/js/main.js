const PlayerDef = {
    "1": 'url("images/red-circle.png")',
    "-1": 'url("images/black-circle.png")', 
    null: 'url("images/white-circle.png")' 
}

//const PlayerDef = {
//    "1": "red", // "/images/red-circle.png",
//    "-1": "black", //"/images/black-circle.png", 
//    null: "white" // "/images/white-circle.png" 
//}

const MessagesObject = {
    "1": " won the game. ",
    "-1": " won the game. ",
    "tie": "All tokens spent. This is draw!"
}

const TOTAL_TOKENS = 21;

/*--- many variables ---*/
var gameBoard = [], playerTurn, winner, tokenCounter;
var bestEstimateLeftConnected = 0;

var columnNumber = ["0", "1", "2", "3", "4", "5", "6"]; /* A ~ G*/
var rowNumber = [0, 1, 2, 3, 4, 5]; //1 - 6

/*--- many html elements ---*/
var message = document.getElementById("message");
var columnButton = document.querySelectorAll("#pushButton button");

var messageBox = document.getElementById("popupBox");
var messageText = document.getElementById("popupText");

var player1TokenCounter = document.getElementById("player1Tokens");
var player2TokenCounter = document.getElementById("player2Tokens");

/*--- many event listeners ---*/
document.getElementById("pushButton").addEventListener("click", clickColumnButton);
document.getElementById("reset").addEventListener("click", function () {
    localStorage.clear();
    location.reload();
    //generateBoard();
    //render();
});

document.getElementById("play-again").addEventListener("click", function () {

    h2.textContent = "00:00:00";
    seconds = 0; minutes = 0; hours = 0;
    player1.totalTokens = TOTAL_TOKENS;
    player2.totalTokens = TOTAL_TOKENS;

    generateBoard();
    render();
});

    var h2 = document.getElementsByTagName('h2')[0],
    //start = document.getElementById('start'),
    //stop = document.getElementById('stop'),
    //clear = document.getElementById('clear'),
    seconds = 0, minutes = 0, hours = 0,
    t;
    
    var player1 = new GamePlayer(1);
    var player2 = new GamePlayer(2);

    checkOldest(player1.birthday, player2.birthday);

    generateBoard();    
    timer();
    render();

function add() {
    seconds++;
    if (seconds >= 60) {
        seconds = 0;
        minutes++;
        if (minutes >= 60) {
            minutes = 0;
            hours++;
        }
    }

    h2.textContent = (hours ? (hours > 9 ? hours : "0" + hours) : "00") + ":" + (minutes ? (minutes > 9 ? minutes : "0" + minutes) : "00") + ":" + (seconds > 9 ? seconds : "0" + seconds);

    timer();
}

function timer() {
    t = setTimeout(add, 1000);
}

    

function calculateTimeDuration() {
    var totalTime; //in seconds
    totalTime = seconds + (minutes * 60) + (hours * 3600);

    return totalTime;
}

function GamePlayer(number){
    this.name = promptName(number);
    this.birthday = promptBirthday(name);
    //this.turn = turn;
    this.totalTokens = TOTAL_TOKENS;
}

function checkOldest(playerOneBirthday, playerTwoBirthday) {
    var dateObj1 = playerOneBirthday;
    var dateObj2 = playerTwoBirthday;

    var temp;

    if (dateObj1 > dateObj2) {
        temp = player1.name;
        player1.name = player2.name;
        player2.name = temp;
    }
}

//prompt player's name
function promptName(number) {
    return name = prompt("Player " + number + " name: ");
}

function promptBirthday(name) {
    var regExpString = "";
    var bday = prompt(name + ", Please enter your birthday (eg., 06/23/1995");

    // matches all string for multple date format
    regExpString = bday.match(/((0?[13578]|10|12|([A-Z][a-z]{2})|([A-Z][a-z]{2,8}))(-|\/|\. | )(([1-9])|(0[1-9])|([12])([0-9]?)|(3[01]?))(-|\/|, )((19)([2-9])(\d{1})|(20)([01])(\d{1})|([8901])(\d{1}))|(0?[2469]|11)(-|\/)(([1-9])|(0[1-9])|([12])([0-9]?)|(3[0]?))(-|\/)((19)([2-9])(\d{1})|(20)([01])(\d{1})|([8901])(\d{1})))/g);
    var bString = regExpString.toString();
    var birthdayArray = bString.split(/\/|-|\. |, | /); // split string using regexp separator

    var month = birthdayArray[0];
    var day = birthdayArray[1];
    var year = birthdayArray[2];

    if (month === "Jan" || month === "January") {
        month = 0; //Jan is 0 in javascript
    } else if (month === "Feb" || month === "February") {
        month = 1;
    } else if (month === "Mar" || month == "March") {
        month = 2;
    } else if (month === "Apr" || month === "April") {
        month = 3;
    } else if (month === "May" || month === "May") {
        month = 4;
    } else if (month === "Jun" || month === "June") {
        month = 5;
    } else if (month === "Jul" || month === "July") {
        month = 6;
    } else if (month === "Aug" || month === "August") {
        month = 7;
    } else if (month === "Sep" || month === "September") {
        month = 8;
    } else if (month === "Oct" || month === "October") {
        month = 9;
    } else if (month === "Nov" || month === "November") {
        month = 10;
    } else if (month === "Dec" || month === "December") {
        month = 11;
    }

    var birthday = new Date(year, month, day);

    return birthday;

}

function clickColumnButton(event) {
    if (winner !== null) {
        return;
    }


    var target = event.target;
    if (target.tagName !== "BUTTON") {
        return;
    }

    if (playerTurn === 1) {
        player1.totalTokens--;
    } else if (playerTurn === -1) {
        player2.totalTokens--;
    }

    var col = parseInt(target.id.charAt(6));
    var row = gameBoard[col].indexOf(null);

    gameBoard[col][row] = playerTurn;
    decidesWinner();
    playerTurn *= -1;
    render();
}

function decidesWinner() {
    for (var colIndex = 0; colIndex < gameBoard.length; colIndex++) {
        for (var rowIndex = 0; rowIndex < gameBoard[colIndex].length; rowIndex++) {
            if (gameBoard[colIndex][rowIndex] === null)
                break;

            //bestEstimateLeftConnected = trackAllCellsLeftFour(colIndex, rowIndex);

            winner = checkFourCellsConnected(colIndex, rowIndex);

            if (winner) {
                break; 
            }   
        }
        if (winner) {
            break;
        }
    }
    if (winner == null && tokenCounter === 42)
        winner = "tie";
}

function trackUpFour(colIndex, rowIndex) {
    
    var presentTokens;
    var leftToConnect;


    presentTokens = Math.abs(gameBoard[colIndex][rowIndex] +
        gameBoard[colIndex][rowIndex + 1] +
        gameBoard[colIndex][rowIndex + 2] +
        gameBoard[colIndex][rowIndex + 3]);

    leftToConnect = 4 - presentTokens;

    return leftToConnect;
}

function trackSideFour(colIndex, rowIndex) {

    var presentTokens;
    var leftToConnect;

    presentTokens = Math.abs(
        gameBoard[colIndex][rowIndex] +
        gameBoard[colIndex + 1][rowIndex] +
        gameBoard[colIndex + 2][rowIndex] +
        gameBoard[colIndex + 3][rowIndex]);

    leftToConnect = 4 - presentTokens;

    return leftToConnect;
}

function trackDiagonalRightFour(colIndex, rowIndex) {
    var presentTokens;
    var leftToConnect;

    presentTokens = Math.abs(
        gameBoard[colIndex][rowIndex] +
        gameBoard[colIndex + 1][rowIndex + 1] +
        gameBoard[colIndex + 2][rowIndex + 2] +
        gameBoard[colIndex + 3][rowIndex + 3]);

    leftToConnect = 4 - presentTokens;

    return leftToConnect;
}

function trackDiagonalLeftFour(colIndex, rowIndex) {
    var presentTokens;
    var leftToConnect;

    presentTokens = Math.abs(
        gameBoard[colIndex][rowIndex] +
        gameBoard[colIndex + 1][rowIndex - 1] +
        gameBoard[colIndex + 2][rowIndex - 2] +
        gameBoard[colIndex + 3][rowIndex - 3]);

    leftToConnect = 4 - presentTokens;

    return leftToConnect;
}

function trackAllCellsLeftFour(colIndex, rowIndex) {
    var a, b, c, d;
    a = trackUpFour(colIndex, rowIndex);
    b = trackSideFour(colIndex, rowIndex);
    c = trackDiagonalRightFour(colIndex, rowIndex);
    d = trackDiagonalLeftFour(colIndex, rowIndex);

    if ((a <= b) && (a <= c) && (a <= d)) {
        return a; // a <= b,c,d,
    } else if ((b <= c) && (b <= d)) {
        return b; // b <= c,d
    } else if ((c <= d)) {
        return c; // c <= d
    } else {
        return d; // d > c
    }
  
}


function checkFourCellsConnected(colIndex, rowIndex) {
    return checkUpConnected(colIndex, rowIndex) || checkSideConnected(colIndex, rowIndex) || checkDiagnoalConnected(colIndex, rowIndex);
}


function checkUpConnected(colIndex, rowIndex) {
    if (rowIndex > 2) {
        return null;
    }

    return Math.abs(
        gameBoard[colIndex][rowIndex] +
        gameBoard[colIndex][rowIndex + 1] +
        gameBoard[colIndex][rowIndex + 2] +
        gameBoard[colIndex][rowIndex + 3]) === 4 ? gameBoard[colIndex][rowIndex] : null; 
}

function checkSideConnected(colIndex, rowIndex) {
    if (colIndex > 3) {
        return null;
    }

    return Math.abs(
        gameBoard[colIndex][rowIndex] +
        gameBoard[colIndex + 1][rowIndex] +
        gameBoard[colIndex + 2][rowIndex] +
        gameBoard[colIndex + 3][rowIndex]) === 4 ? gameBoard[colIndex][rowIndex] : null;
  
}

function checkDiagnoalConnected(colIndex, rowIndex) {
    if (colIndex > 3) {
        return null;
    }

    var isRightDiagonalConnected = Math.abs(
        gameBoard[colIndex][rowIndex] +
        gameBoard[colIndex + 1][rowIndex + 1] +
        gameBoard[colIndex + 2][rowIndex + 2] +
        gameBoard[colIndex + 3][rowIndex + 3]) === 4;

    var isLeftDiagonalConnected = Math.abs(
        gameBoard[colIndex][rowIndex] +
        gameBoard[colIndex + 1][rowIndex - 1] +
        gameBoard[colIndex + 2][rowIndex - 2] +
        gameBoard[colIndex + 3][rowIndex - 3]) === 4;

    return isRightDiagonalConnected || isLeftDiagonalConnected ? gameBoard[colIndex][rowIndex] : null;
}

function render() {
    gameBoard.forEach(function (col, colIndex) {
        col.forEach(function (cell, rowIndex) {
            var td = document.getElementById(`c${colIndex}r${rowIndex}`);
            td.style.backgroundImage = PlayerDef[cell];
        });
        columnButton[colIndex].style.visibility = col.includes(null) ? "visible" : "hidden";
    });

    player1TokenCounter.textContent = "Remaining Token: " + player1.totalTokens;
    player2TokenCounter.textContent = "Remaining Token: " + player2.totalTokens;

    if (winner) {
        clearTimeout(t);
        var timeTook = calculateTimeDuration();

        var champion;
        if (playerTurn === 1) {
            champion = player1.name;
        } else if (playerTurn === -1) {
            champion = player2.name;
        }


        alert("Congratulations! " + champion + MessagesObject[winner] + "It took " + timeTook + "s.");

        removeTable();
        updateLocalStorage(playerTurn);
    } else {
        //alert("needs" + bestEstimateLeftConnected + "to Connect.");
        //alert(`${playerTurn === 1 ? player1.name : player2.name} needs ${bestEstimateLeftConnected} tokens to Connect Four!`);
        alert(`Click to begin ${playerTurn === 1 ? player1.name : player2.name}'s turn`);

    }
}

function generateBoard() {

    playerTurn = 1;
    winner = null;

    
    var table = document.createElement("table");
    table.setAttribute("id", "gameboard");
    var columnCount =7;
    var rowCount = 6;

   var i;
   var j;

    for (i = 0; i < rowCount; i++) {
        
        var row = table.insertRow(i);
        //gameBoard[i] = [];

        for (j = 0; j < columnCount; j++) {
           //var row = table.insertRow(j);
            var td = document.createElement("td");
            td.setAttribute("id", "c" + columnNumber[j] + "r" + rowNumber[rowCount - i - 1]);

            row.appendChild(td);

            //gameBoard[i][j] = null;
        }
    }

    document.body.appendChild(table);

    for (var col = 0; col < columnCount; col++) {
        gameBoard[col] = [];

        for (var row = 0; row < rowCount; row++) {
            gameBoard[col][row] = null;
        }
    }
}

function updateLocalStorage(playerTurn) {
    var champion;
    if (playerTurn === 1) {
        champion = player1.name;
    } else if (playerTurn === -1) {
        champion = player2.name;
    }

    var duration; 
    duration = calculateTimeDuration();

    if (localStorage.length <= 10) {
        if (localStorage.length < 10) {
            localStorage.setItem(duration, champion);//player1.name + ", " + player2.name);
            console.log("here!");
        } else if (localStorage.length == 10 && (localStorage.key(0) < duration)) {
            localStorage.removeItem(localStorage.key(0));
            localStorage.setItem(duration, champion); //player1.name + ", " + player2.name);
            console.log("there!");
        }
    }

    var list = [[], []];
    for (var i = 0; i < localStorage.length; i++) {
        list[0][i] = localStorage.key(i);
        list[1][i] = localStorage.getItem(localStorage.key(i));
    }

    var leaderBoardPopUp = "Leaderboard: \n";
    for (var j = [localStorage.length - 1]; j >= 0; j--) {
        console.log(j);
        var temp = list[1][j] + ": " + list[0][j] + "s \n";
        leaderBoardPopUp = leaderBoardPopUp + temp;
    }

    alert(leaderBoardPopUp);
    //exit();
}

function removeTable() {
    var removeTable = document.getElementById("gameboard");
    var parent = removeTable.parentElement;

    parent.removeChild(removeTable);
}




///* start button */
//start.onclick = timer;

///* stop button */
//stop.onclick = function () {
//    clearTimeout(t);
//}

///* clear button */
//clear.onclick = function () {
//    h1.textcontent = "00:00:00";
//    seconds = 0; minutes = 0; hours = 0;
//}