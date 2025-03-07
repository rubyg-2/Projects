/*
KEY FEATURE: Personalize it
https://stackoverflow.com/questions/26107125/cannot-read-property-addeventlistener-of-null
*/


//Will need to explain that sometimes the DOM doesn't load all the way, so the browser can't find the element for JavaScript to use

/*
REFACTOR ITEM 2: 
Create a separate function for displaying the script.
Be sure to update function names.
*/



//Move these variables to the top
var factList = [ 
  "Just 3% of computing-related jobs are held by African-American women, 6% held by Asian women and 2% held by Hispanic women.", 
  "The research by PWC reports that of all the female respondents, only 27% said that they would consider a tech career", 
  "The gap is even bigger when it comes to courses like engineering. 13% of the surveyed male university students were taking engineering courses, compared to just 2% of female university students", 
  "In the mid-1980s, 37% of computer science majors were women; in 2012, 18%. Women only earn about 12% of computer science degrees nationally", 
  "The average percentage of women working in the tech industry is 30%, based on diversity reports published by 11 of the world's largest tech companies in 2014"
];

var fact = document.getElementById("fact");
var factBtn = document.getElementById("factBtn");
var count = 0;

if (factBtn) {
  factBtn.addEventListener("click", displayFact);
}

function displayFact() {
  fact.innerHTML = factList[count];
  count++;
  if (count == factList.length) {
    count = 0;
  }
}
