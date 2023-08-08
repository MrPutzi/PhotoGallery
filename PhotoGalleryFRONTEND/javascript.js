/**
 * FUNCTIONS * 
 */

function getMyCourses() {
	let xhr = new XMLHttpRequest();
	xhr.open("GET", "http://localhost:8080/mycourses");
	const token = localStorage.getItem("token");
	xhr.setRequestHeader("Token", "Bearer " + token);
	xhr.onload = function() {
		if (xhr.status === 200) {
			const courses = JSON.parse(xhr.responseText);
			const coursesTable = document.getElementById("courses-table");

			// Clear the table before populating it with data
			coursesTable.innerHTML = "";

			// Add table headers
			const tableHeaders = document.createElement("tr");
			const nameHeader = document.createElement("th");
			nameHeader.textContent = "Course Name";
			tableHeaders.appendChild(nameHeader);
			const descriptionHeader = document.createElement("th");
			descriptionHeader.textContent = "Description";
			tableHeaders.appendChild(descriptionHeader);
			const startDateHeader = document.createElement("th");
			startDateHeader.textContent = "Start Date";
			tableHeaders.appendChild(startDateHeader);
			const attendHeader = document.createElement("th");
			attendHeader.textContent = "Attend";
			tableHeaders.appendChild(attendHeader);
			coursesTable.appendChild(tableHeaders);

			// Loop through the courses and add them to the table
			courses.forEach((course) => {
				const row = document.createElement("tr");

				const nameCell = document.createElement("td");
				nameCell.textContent = course.courseTitle;
				nameCell.classList.add("course-cell");
				row.appendChild(nameCell);

				const descriptionCell = document.createElement("td");
				descriptionCell.textContent = course.courseDescription;
				descriptionCell.classList.add("course-cell");
				row.appendChild(descriptionCell);

				const startDateCell = document.createElement("td");
				startDateCell.textContent = course.courseStartDate;
				startDateCell.classList.add("course-cell");
				row.appendChild(startDateCell);

				const attendButton = document.createElement("button");
				attendButton.textContent = "Unattend";
				const courseId = course.courseId; // Store the courseId
				attendButton.addEventListener("click", () => {
					unattendCourse(courseId, seat);
				});
				const attendCell = document.createElement("td");
				attendCell.appendChild(attendButton);
				attendCell.classList.add("course-cell");
				row.appendChild(attendCell);

				coursesTable.appendChild(row);

				const separator = document.createElement("tr");
				const separatorCell = document.createElement("td");
				separatorCell.setAttribute("colspan", "4");
				separatorCell.classList.add("separator");
				separator.appendChild(separatorCell);
				coursesTable.appendChild(separator);

				// Add hover styles to each course cell
				const courseCells = row.querySelectorAll(".course-cell");
				courseCells.forEach((cell) => {
					cell.addEventListener("mouseenter", () => {
						cell.classList.add("cell-hover");
					});
					cell.addEventListener("mouseleave", () => {
						cell.classList.remove("cell-hover");
					});
				});
			});

			/*displayCourses(mycourses, true); // Pass 'true' to indicate that it's for my courses*/
		} else {
			console.error(xhr.statusText);
		}
	};
	xhr.onerror = function() {
		console.error(xhr.statusText);
	};
	xhr.send();
}

function getCourses() {
	let xhr = new XMLHttpRequest();
	xhr.open("GET", "http://localhost:8080/courses");
	const token = localStorage.getItem("token");
	xhr.setRequestHeader("Token", "Bearer " + token);
	xhr.onload = function() {
		if (xhr.status === 200) {
			const courses = JSON.parse(xhr.responseText);
			displayCourses(courses); // Pass 'false' to indicate that it's for all courses
		} else {
			console.error(xhr.statusText);
		}
	};
	xhr.onerror = function() {
		console.error(xhr.statusText);
	};
	xhr.send();
}

function createTableFromJSON(response) {
	let data = JSON.parse(response);
	let table = document.createElement('table');
	table.classList.add('table');
	let headerRow = document.createElement('tr');
	let headerCells = ['Title', 'Description', 'Lector', 'Date', 'Participants'];

	for (let i = 0; i < headerCells.length; i++) {
		let cell = document.createElement('th');
		cell.textContent = headerCells[i];
		headerRow.appendChild(cell);
	}

	table.appendChild(headerRow);

	let dataRow = document.createElement('tr');
	let dataCells = [data.title, data.description, data.lector, data.date, data.participants.join(', ')];

	for (let i = 0; i < dataCells.length; i++) {
		let cell = document.createElement('td');
		cell.textContent = dataCells[i];
		dataRow.appendChild(cell);
	}

	table.appendChild(dataRow);

	return table;
}

function changePassword(username, password, nepassword) {
	const request = new XMLHttpRequest();
	request.open("POST", "http://localhost:8080/changepassword");
	request.setRequestHeader("Content-Type", "application/json");
	let token = localStorage.getItem("token");
	request.setRequestHeader("Token", "Bearer" + token);
	request.addEventListener("load", function() {
		var response = request.responseText;
		forward(response);
	});
	request.send(JSON.stringify({
		"username": username,
		"password": password,
		"newpassword": nepassword
	}));
}

function forward(response) {
	const token = response;
	console.log(token);
	let request = new XMLHttpRequest();
	request.open("GET", "http://localhost:8080/homepage");
	request.setRequestHeader("Token", token);
	request.addEventListener("load", function() {
		if (request.status === 200) {
			const token = localStorage.getItem("token");
			window.location.href = "homepage.html";
		} else {
			console.log("Request failed with status:", request.status);
		}
	});
	request.send();
}

function register(username, password) {
	var request = new XMLHttpRequest();
	request.open("POST", "http://localhost:8080/register");
	request.setRequestHeader("Content-Type", "application/json");
	request.addEventListener("load", function() {
		var response = request.responseText;
		forward(response);
	});
	request.send(JSON.stringify({
		"username": username,
		"password": password
	}));
}

function createCourseBlock(course) {
	const courseBlock = document.createElement("div");
	courseBlock.classList.add("course-block");

	const courseName = document.createElement("h2");
	courseName.innerText = course.courseName;

	const courseDescription = document.createElement("p");
	courseDescription.innerText = course.courseDescription;

	const courseStartDate = document.createElement("p");
	courseStartDate.innerText = course.courseStartDate;

	courseBlock.appendChild(courseName);
	courseBlock.appendChild(courseDescription);
	courseBlock.appendChild(courseStartDate);

	return courseBlock;
}

function displayCourses(xhrResponse) {
	var xhr = new XMLHttpRequest();
	xhr.open("GET", "http://localhost:8080/courses");
	xhr.setRequestHeader("Content-Type", "application/json");
	var token = localStorage.getItem("token");
	console.log(token);
	xhr.setRequestHeader("Token", "Bearer " + token);
	xhr.onload = function() {
		if (xhr.status === 200) {
			const courses = JSON.parse(xhr.responseText);
			const coursesTable = document.getElementById("courses-table");

			// Clear the table before populating it with data
			coursesTable.innerHTML = "";

			// Add table headers
			const tableHeaders = document.createElement("tr");
			const nameHeader = document.createElement("th");
			nameHeader.textContent = "Course Name";
			tableHeaders.appendChild(nameHeader);
			const descriptionHeader = document.createElement("th");
			descriptionHeader.textContent = "Description";
			tableHeaders.appendChild(descriptionHeader);
			const startDateHeader = document.createElement("th");
			startDateHeader.textContent = "Start Date";
			tableHeaders.appendChild(startDateHeader);
			const attendHeader = document.createElement("th");
			attendHeader.textContent = "Attend";
			tableHeaders.appendChild(attendHeader);
			coursesTable.appendChild(tableHeaders);

			// Loop through the courses and add them to the table
			courses.forEach((course) => {
				const row = document.createElement("tr");

				const nameCell = document.createElement("td");
				nameCell.textContent = course.courseTitle;
				nameCell.classList.add("course-cell");
				row.appendChild(nameCell);

				const descriptionCell = document.createElement("td");
				descriptionCell.textContent = course.courseDescription;
				descriptionCell.classList.add("course-cell");
				row.appendChild(descriptionCell);

				const startDateCell = document.createElement("td");
				startDateCell.textContent = course.courseStartDate;
				startDateCell.classList.add("course-cell");
				row.appendChild(startDateCell);

				const attendButton = document.createElement("button");
				attendButton.textContent = "Attend";
				const courseId = course.courseId; // Store the courseId
				attendButton.addEventListener("click", () => {
					openPopup(courseId); // Pass the courseId to openPopup function
				});
				const attendCell = document.createElement("td");
				attendCell.appendChild(attendButton);
				attendCell.classList.add("course-cell");
				row.appendChild(attendCell);

				coursesTable.appendChild(row);

				const separator = document.createElement("tr");
				const separatorCell = document.createElement("td");
				separatorCell.setAttribute("colspan", "4");
				separatorCell.classList.add("separator");
				separator.appendChild(separatorCell);
				coursesTable.appendChild(separator);

				// Add hover styles to each course cell
				const courseCells = row.querySelectorAll(".course-cell");
				courseCells.forEach((cell) => {
					cell.addEventListener("mouseenter", () => {
						cell.classList.add("cell-hover");
					});
					cell.addEventListener("mouseleave", () => {
						cell.classList.remove("cell-hover");
					});
				});
			});
		} else {
			console.error(xhr.statusText);
		}
	};
	xhr.onerror = function() {
		console.error(xhr.statusText);
	};
	xhr.send();
}

function attendCourse(courseId, seatNumber) {
	const xhr = new XMLHttpRequest();
	xhr.open("PUT", `http://localhost:8080/course/${courseId}/reserve`);
	xhr.setRequestHeader("Content-Type", "application/json");
	const token = localStorage.getItem("token");
	xhr.setRequestHeader("Token", token);

	const requestBody = JSON.stringify({
		seat: seatNumber
	}); // Use the seatNumber parameter

	xhr.onload = function() {
		if (xhr.status === 200) {
			alert("Seat reserved!");
		} else if (xhr.status === 400) {
			alert(xhr.responseText);
		} else {
			console.error(xhr.statusText);
		}
	};

	xhr.onerror = function() {
		console.error(xhr.statusText);
	};

	xhr.send(requestBody); // Send the request body with the request

	xhr.addEventListener("load", function() {
		alert("Seat " + seatNumber + " reserved!");
	});
}

function unattendCourse(courseId, seat) {
	const xhr = new XMLHttpRequest();
	xhr.open("PUT", `http://localhost:8080/course/${courseId}/unreserve`);
	xhr.setRequestHeader("Content-Type", "application/json");
	const token = localStorage.getItem("token");
	xhr.setRequestHeader("Token", token);
	localStorage.getItem("seatNumber", seat);

	const requestBody = JSON.stringify({
		seat: seat
	}); // Create the request body object

	xhr.onload = function() {
		if (xhr.status === 200) {
			alert("Seat unreserved!");
		} else if (xhr.status === 400) {
			alert(xhr.responseText);
		} else {
			console.error(xhr.statusText);
		}
	}

	xhr.onerror = function() {
		console.error(xhr.statusText);
	}

	xhr.send(requestBody); // Send the request body with the request
	xhr.addEventListener("load", function() {
		alert("Seat" + seat + "unreserved!");
	});



}
/*
function getCourseJson(form) {
  const title = form.querySelector('input[name="title"]').value;
  const description = form.querySelector('input[name="description"]').value;
  const lector = form.querySelector('input[name="lector"]').value;
  const date = form.querySelector('input[name="date"]').value;

  if (title === null || description === null || lector === null || date === null) {
    throw new Error("Please fill out all fields");
  }

  const participants = [];
  for (let i = 1; i <= 30; i++) {
    participants.push({
      seat: i
    });
  }

  return {
    title,
    description,
    lector,
    date,
    participants
  };
}
*/
/*
function openPopup(courseId) {
	const popupContainer = document.createElement("div");
	popupContainer.className = "popup-container";
	const popupContent = document.createElement("div");
	popupContent.className = "popup-content";

	const h2 = document.createElement("h2");
	h2.textContent = "Select a seat:";
	popupContent.appendChild(h2);


	const seatButtonsContainer = document.getElementById("seat-buttons");

	for (let row = 1; row <= 4; row++) {
		const rowDiv = document.createElement("div");
		rowDiv.classList.add("seat-row");

		for (let seat = 1; seat <= 3; seat++) {
			const button = document.createElement("button");
			const seatNumber = (row - 1) * 3 + seat;
			button.textContent = seatNumber;
			button.dataset.seat = seatNumber; // Set the data-seat attribute to store the seat number
			button.addEventListener("click", () => {
				attendCourse(courseId, seatNumber);
			});
			rowDiv.appendChild(button);
			localStorage.setItem("seat", seatNumber);
		}

		seatButtonsContainer.appendChild(rowDiv);
	}
seatButtonsContainer.appendChild(popupContent);
	// Call getParticipants function to disable taken seats
	getParticipants(courseId);

	const closeButton = document.createElement("button");
	closeButton.textContent = "Close";
	closeButton.addEventListener("click", () => {
		container.remove();
		seatButtonsContainer.remove();
	});
	popupContent.appendChild(closeButton);
	const container = document.getElementById("popupContainer");
	popupContainer.appendChild(popupContent);
	container.appendChild(popupContainer);
  
}
*/
/*
function openPopup(courseId) {
	const popupContainer = document.createElement("div");
	popupContainer.className = "popup-container";
	const popupContent = document.createElement("div");
	popupContent.className = "popup-content";

	const h2 = document.createElement("h2");
	h2.textContent = "Select a seat:";
	popupContent.appendChild(h2);


	const seatButtonsContainer = document.getElementById("seat-buttons");

	for (let row = 1; row <= 4; row++) {
		const rowDiv = document.createElement("div");
		rowDiv.classList.add("seat-row");

		for (let seat = 1; seat <= 3; seat++) {
			const button = document.createElement("button");
			const seatNumber = (row - 1) * 3 + seat;
			button.textContent = seatNumber;
			button.dataset.seat = seatNumber; // Set the data-seat attribute to store the seat number
			button.addEventListener("click", () => {
				attendCourse(courseId, seatNumber);
			});
			rowDiv.appendChild(button);
			localStorage.setItem("seat", seatNumber);
		}

		seatButtonsContainer.appendChild(rowDiv);
	}
seatButtonsContainer.appendChild(popupContent);
	// Call getParticipants function to disable taken seats
	getParticipants(courseId);

	const closeButton = document.createElement("button");
	closeButton.textContent = "Close";
	closeButton.addEventListener("click", () => {
		container.innerHTML = "";
		seatButtonsContainer.innerHTML = "";
	});
	popupContent.appendChild(closeButton);
	const container = document.getElementById("popupContainer");
	popupContainer.appendChild(popupContent);
	container.appendChild(popupContainer);
  
}
*/
function openPopup(courseId) {
	const popupContainer = document.createElement("div");
	popupContainer.className = "popup-container";
	const popupContent = document.createElement("div");
	popupContent.className = "popup-content";
  
	const h2 = document.createElement("h2");
	h2.textContent = "Select a seat:";
	popupContent.appendChild(h2);
  
	const seatButtonsContainer = document.getElementById("seat-buttons");
	seatButtonsContainer.innerHTML = "";
  
	for (let row = 1; row <= 6; row++) {
	  const rowDiv = document.createElement("div");
	  rowDiv.classList.add("seat-row");
  
	  for (let seat = 1; seat <= 5; seat++) {
		const button = document.createElement("button");
		const seatNumber = (row - 1) * 5 + seat;
		button.textContent = seatNumber;
		button.dataset.seat = seatNumber; // Set the data-seat attribute to store the seat number
		button.addEventListener("click", () => {
		  attendCourse(courseId, seatNumber);
		  container.innerHTML = "";
		  seatButtonsContainer.innerHTML = "";
		});
		rowDiv.appendChild(button);
		localStorage.setItem("seat", seatNumber);
	  }
  
	  seatButtonsContainer.appendChild(rowDiv);
	}
  
	seatButtonsContainer.appendChild(popupContent);
  
	// Call getParticipants function to disable taken seats
	getParticipants(courseId);
  
	const closeButton = document.createElement("button");
	closeButton.textContent = "Close";
	closeButton.addEventListener("click", () => {
	  container.innerHTML = "";
	  seatButtonsContainer.innerHTML = "";
	});
	popupContent.appendChild(closeButton);
  
	const container = document.getElementById("popupContainer");
	popupContainer.appendChild(popupContent);
	container.appendChild(popupContainer);
  }
  
  function getParticipants(courseId) {
	const xhr = new XMLHttpRequest();
	xhr.open("GET", `http://localhost:8080/course/${courseId}/participants`);
	xhr.setRequestHeader("Content-Type", "application/json");
	const token = localStorage.getItem("token");
	xhr.setRequestHeader("Token", "Bearer " + token);
	xhr.onload = function () {
	  if (xhr.status === 200) {
		const response = JSON.parse(xhr.responseText);
  
		// Check if response is an array
		if (Array.isArray(response)) {
		  response.forEach(function (participant) {
			if (participant.name !== "") {
			  const seat = participant.seat;
			  const seatButton = document.querySelector(`button[data-seat="${seat}"]`);
			  if (seatButton) {
				seatButton.classList.add("taken-seat");
				seatButton.disabled = true;
			  }
			}
		  });
		}
	  } else {
		console.error("Error fetching participants:", xhr.status);
	  }
	};
	xhr.send();
  }
  

/*

function createCourseForm() {

    const formContainer = document.getElementById("courses-table");
    const table = document.createElement("table");
    formContainer.innerHTML = 

    const createRow = (labelText, inputType) => {
    const row = document.createElement("tr");

    const labelCell = document.createElement("td");
    labelCell.textContent = labelText;

    const inputCell = document.createElement("td");
    const input = document.createElement("input");
    input.type = inputType;
    input.required = true;
    inputCell.appendChild(input);

    row.appendChild(labelCell);
    row.appendChild(inputCell);
    return row;
  };

  const titleRow = createRow("Title:", "text");
  const descriptionRow = createRow("Description:", "text");
  const dateRow = createRow("Date:", "date");

  table.appendChild(titleRow);
  table.appendChild(descriptionRow);
  table.appendChild(dateRow);
  formContainer.appendChild(table);

  const submitButton = document.createElement("button");
  submitButton.textContent = "Submit";
  submitButton.addEventListener("click", () => {
    const title = titleRow.querySelector("input").value;
    const description = descriptionRow.querySelector("input").value;
    const lector = "Alice"; // Automatically set the lector to "Alice"
    const date = dateRow.querySelector("input").value;
    const seat = 1; // Assuming the participant's seat is always 1

    if (title && description && date) {
      const course = {
        title: title,
        description: description,
        lector: lector,
        date: date,
        participants: [
          {
            seat: seat
          }
        ]
      };

       // Send the request to the server
  
       const xhr = new XMLHttpRequest();
      xhr.open("POST", "http://localhost:8080/course/new");
      xhr.setRequestHeader("Content-Type", "application/json");
      const token = localStorage.getItem("token");
      xhr.setRequestHeader("Token", "Bearer " + token);
      xhr.onload = function() {
        if (xhr.status === 200) {
          alert("Course added successfully!");
        } else {
          console.error("Error adding course:", xhr.status);
        }
      };
      xhr.onerror = function() {
        console.error("Error adding course:", xhr.statusText);
      };
      xhr.send(JSON.stringify(course));
    } else {
      alert("Please fill in all fields.");
    }
  });

  formContainer.appendChild(submitButton);

  // Add the form container to the document body
  document.body.appendChild(formContainer);
}


function attendCourse(courseId, seat) {
  const xhr = new XMLHttpRequest();
  xhr.open("PUT", `http://localhost:8080/course/${courseId}/reserve`);
  xhr.setRequestHeader("Content-Type", "application/json");
  const token = localStorage.getItem("token");
  xhr.setRequestHeader("Token", token);
//get the seat from the json
var seat = document.getElementById("seat").value;
  const requestBody = JSON.stringify({ seat: seat }); // Create the request body object

  xhr.onload = function() {
    if (xhr.status === 200) {
      alert("Seat reserved!");
    } else if (xhr.status === 400) {
      alert(xhr.responseText);
    } else {
      console.error(xhr.statusText);
    }
  };

  xhr.onerror = function() {
    console.error(xhr.statusText);
  };

  xhr.send(requestBody); // Send the request body with the request
  xhr.addEventListener("load", function() {
    alert("Seat" + seat + "reserved!");
  });
}

*/

/*  * LINKS * EVENTLISTENERS *  */

 

const coursesTable = document.getElementById("courses-table");

const unattendAllLink = document.getElementById("unattendAll");

const userIcon = document.querySelector('.user-icon');

const menuOptions = document.querySelector('.menu-options');

const myCoursesLink = document.getElementById("my-Courses");

const getCoursesLink = document.getElementById("get-Courses");

const getParticipantsLink = document.getElementsByClassName("course-cell");

getCoursesLink.addEventListener("click", function() {
	getCourses();
});

myCoursesLink.addEventListener("click", function() {
	getMyCourses();
});
const joinButton = document.getElementById("joinButton");
if (joinButton) {
	joinButton.onclick = function() {
		const courseId = joinButton.getAttribute("data-course-id");
		const username = localStorage.getItem("username");
		const token = localStorage.getItem("token");
		const xhr = new XMLHttpRequest();
		xhr.open("PUT", `http://localhost:8080/course/${courseId}/reserve/${username}`);
		xhr.setRequestHeader("Content-Type", "application/json");
		xhr.setRequestHeader("Token", token);
		xhr.onload = function() {
			if (xhr.status === 200) {
				console.log("Seat reserved");
			} else {
				console.error(xhr.statusText);
			}
		};
		xhr.send();
	};
}
document.getElementById("logout-button").onclick = function() {
	var token = localStorage.getItem("token");
	var username = localStorage.getItem("username");
	var request = new XMLHttpRequest();
	request.open("POST", "http://localhost:8080/logout/" + username);
	request.setRequestHeader("Content-Type", "application/json");
	request.setRequestHeader("Token", "Bearer " + token);
	request.addEventListener("load", function() {
		if (request.status === 200) {
			window.location.href = "index.html";
		} else {
			console.log("Request failed with status:", request.status);
		}
	});
	request.send();
};
unattendAllLink.addEventListener("click", () => {


	const xhr = new XMLHttpRequest();
	xhr.open("PUT", "http://localhost:8080/course/unattend-all");
	xhr.setRequestHeader("Content-Type", "application/json");
	const token = localStorage.getItem("token");
	xhr.setRequestHeader("Token", "Bearer " + token);
	xhr.onload = function() {
		if (xhr.status === 200) {
			alert("Unattended successfully from all courses!");
		} else {
			console.error("Error unattending all participants:", xhr.status);
		}
	}
	xhr.onerror = function() {
		console.error("Error unattending all participants:", xhr.statusText);
	}
	xhr.send();
});
coursesTable.innerHTML = "";
function createCourseForm() {
	const formContainer = document.getElementById("courses-table");
	formContainer.innerHTML = `
    <form id="course-form">
      <div>
        <label for="title">Title:</label>
        <input type="text" id="title" required>
      </div>
      <div>
        <label for="description">Description:</label>
        <input type="text" id="description" required>
      </div>
      
      <div>
        <label for="date">Date:</label>
        <input type="date" id="date" required>
      </div>
      <button type="submit">Submit</button>
    </form>
  `;

	const courseForm = document.getElementById("course-form");
	courseForm.addEventListener("submit", (event) => {
		event.preventDefault();

		const title = document.getElementById("title").value;
		const description = document.getElementById("description").value;
		const lector = localStorage.getItem("username");
		const date = document.getElementById("date").value;
		const seat = 1; // Assuming the participant's seat is always 1

		if (title && description && date) {
			const course = {
				title: title,
				description: description,
				lector: lector,
				date: date,
				participants: [{
					seat: seat
				}]
			};

			// Send the request to the server
			fetch("http://localhost:8080/course/new", {
					method: "POST",
					headers: {
						"Content-Type": "application/json",
						"Token": "Bearer " + localStorage.getItem("token")
					},
					body: JSON.stringify(course)
				})
				.then((response) => {
					if (response.ok) {
						alert("Course added successfully!");
					} else {
						throw new Error("Error adding course: " + response.status);
					}
				})
				.catch((error) => {
					console.error(error);
					alert("Error adding course. Please try again.");
				});
		} else {
			alert("Please fill in all fields.");
		}
	});
}

