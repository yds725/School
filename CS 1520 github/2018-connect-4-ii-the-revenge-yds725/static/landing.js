var timeoutID;
var timeout = 3000;

function setup() {
	document.getElementById("create_game_button").addEventListener("click", addGame, true);
	timeoutID = window.setTimeout(makePoll(), timeout)
}

function addGame() {
	var httpRequest = new XMLHttpRequest();

	if (!httpRequest) {
		alert('Giving up :( cannot create XMLHttp instance');
		return false;
	}

	httpRequest.onreadystatechange = function() { alertResult(httpRequest) };

	httpRequest.open("POST", "/create_game/");
	httpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

	var data;
	data = "player1_name=" + document.getElementById('create_game_button').value + "&player2_name=" + document.getElementById('player2_username').value;
	console.log(data);
	httpRequest.send(data);
}

function alertResult(httpRequest) {
	console.log(httpRequest.readyState);
	if (httpRequest.readyState === XMLHttpRequest.DONE) {
		if (httpRequest.status === 200) {
            console.log("Working great")
            clearInput();
		}
		else {
			console.log("Not working");
		}
	}
}

function makePoll() {

	var httpRequest = new XMLHttpRequest();
	
	if (!httpRequest) {
		alert('Giving up :( cannot create XMLHttp instance');
		return false;
	}

	httpRequest.onreadystatechange = function() { handlePoll(httpRequest) };

	httpRequest.open("GET", "/show_game/");
    //httpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    httpRequest.send();

}

function handlePoll(httpRequest) {
	if (httpRequest.readyState === XMLHttpRequest.DONE) {
		if (httpRequest.status === 200) {
			console.log(httpRequest.response);
            
            var table = document.getElementById("game_list");
            //console.log(msg.pub_date);
            while(table.rows.length > 0) {
				table.deleteRow(0);
            }
            
            var rows = JSON.parse(httpRequest.responseText);
			for (var i = 0; i < rows.length; i++) {
				addRow(rows[i]);
			}
			//}
			timeoutID = window.setTimeout(makePoll, timeout);
		} else {
			console.log("There was a problem in poll request. You have to refresh the page");
			//timeoutID = window.setTimeout(makePoll, timeout);
		}
	}
}

function clearInput() {
	document.getElementById("player2_username").value = "";
	document.getElementById("create_game_button").value = "";
	//document.getElementById("c").value = "";
}

function addRow(row) {
	var tableRef = document.getElementById("game_list");
	var newRow   = tableRef.insertRow();

	var newCell, newText;
	for (var i = 0; i < row.length; i++) {
		newCell  = newRow.insertCell();
		newText  = document.createTextNode(row[i]);
        newCell.appendChild(newText);
        
        buttonCell = newRow.insertCell();
        newButton = document.createElement("button");
    	newButton.type = "submit";
    	newButton.name = "game_button";
        newButton.value = row[1];
        console.log(row[1]);
    		newButton.innerHTML = "play game";
    		newButton.id = "game_button";
    		buttonCell.appendChild(newButton);
	}
}

window.addEventListener("load", setup, true);

















// var timeoutID;
// var timeout = 4500;

// function setup()
// {
//     document.getElementById("create_game_button").addEventListener("click", addNewGame, true);
// }

// function addNewGame(){
//     var player2User = document.getElementById("player2_username").value;
//     var player1User = document.getElementById("create_game_button").value;

//     var data;

//     data = '{"player1_name":"' + player1User + '","player2_name":"' + player2User + '"}';
//     window.clearTimeout(timeoutID);

//     makeRequest("POST", "/create_game/", 200, poller, data);
//     document.getElementById("player2_username").value = "";
// }

// function makeRequest(method, target, returnCode, callback, data){
//     var httpRequest = new XMLHttpRequest();

// 	if (!httpRequest) {
// 		alert("Cannot create XMLHttpRequest. Error!");
// 		return false;
// 	}
// 	// alert("stop 1");
// 	httpRequest.onreadystatechange = makeHandler(httpRequest, returnCode, callback);
// 	// alert("stop 2");

// 	httpRequest.open(method, target);
// 	// alert("stop 3");

// 	if (data){
// 	    alert(data);
// 		httpRequest.setRequestHeader('Content-Type', 'application/json');
// 		httpRequest.send(data);
// 	}
// 	else {
// 		httpRequest.send();
// 	} 
// }

// function makeHandler(httpRequest, returnCode, callback){
//     function handler() {
//         if (httpRequest.readyState === XMLHttpRequest.DONE) {
// 			if (httpRequest.status === returnCode) {
// 				console.log("Received this JSON response text:  " + httpRequest.responseText);
// 				callback(httpRequest.responseText);
// 			} else {
// 				//alert("Some problem handling this response text. Please refresh the page!");
// 			}
// 		}
// 	}
// 	return handler;
// }

// function poller(){
//     makeRequest("GET", "/", 200, repopulate);
// }

// function addCell(row, string){
//     var newCell = row.insertCell();
// 	var newText = document.createTextNode(string);
//     newCell.className = "game_table_cell"
// 	newCell.appendChild(newText);
// }

// function repopulate(responseText){
//     console.log("repopulating table (to update without refresh)");
//     var games = JSON.parse(responseText);
//     var table = document.getElementById("game_list")
//     var newRow, newCell, g, game, gameName, userName;

//     while (table.rows.length > 0) {
// 		table.deleteRow(0);
//     }
    
//     if(games.length == 0){
//         newRow = table.insertRow();
//         //noRoomsMessage(newRow);
//     } else {
//         for (g in games) {
//             newRow = table.insertRow();
//             gameName = games[g]["player1_name"] + ".vs." + games[g]["player2_name"];
//             userName = games[g]["player2_name"]
//     		addCell(newRow, gameName);
    	
//     		//roomname_under = roomname.replace(/\s/, "_");

//     		newCell = newRow.insertCell();
//     		newButton = document.createElement("button");
//     		newButton.type = "submit";
//     		newButton.name = "play_game_button";
//     		newButton.value = userName;
//     		newButton.innerHTML = "Play Game";
//     		newButton.id = "play_game_button";
//     		newCell.appendChild(newButton);
//         }
//     }

//     timeoutID = window.setTimeout(poller, timeout);

// }

// //load event
// window.addEventListener("load", setup, true);


