// By Frode Eika Sandnes, Oslo Metropolitan University, Oslo, Norway, January 2021

// modified for full code
function soundex(name)
	{
    let s = [];
    let si = 1;
    let c;

    //              ABCDEFGHIJKLMNOPQRSTUVWXYZ
    let mappings = "01230120022455012623010202";

    s[0] = name[0].toUpperCase();

    for(let i = 1, l = name.length; i < l; i++)
		{
        c = (name[i].toUpperCase()).charCodeAt(0) - 65;

        if(c >= 0 && c <= 25)
			{
            if(mappings[c] != '0')
				{
                if(mappings[c] != s[si-1])
					{
                    s[si] = mappings[c];
                    si++;
					}

/*                if(si > 3)
					{
                    break;
					}*/
				}
			}
		}

    if(si <= 3)
		{
        while(si <= 3)
			{
            s[si] = '0';
            si++;
			}
		}

    return s.join("");
	}

String.prototype.hashCode = function()
	{
    var hash = 0;
    for (var i = 0; i < this.length; i++) 
		{
        var character = this.charCodeAt(i);
        hash = ((hash<<5)-hash)+character;
        hash = hash & hash; // Convert to 32bit integer
		}
    return hash;
	}	

	
function encode() 
	{
	var name = document.forms["encodeform"]["name"].value;
	var salt = document.forms["encodeform"]["salt"].value;
	var digits = document.forms["encodeform"]["digits"].value;
	name = name.replace(",","");
	var parts = name.split(" ");
	parts.sort();
	var x;
	var code = "";
	for (x of parts)
		{
		code += soundex(x);
		}
	code += salt;
	var hash = "0000000"+code.hashCode();
	var id = hash.substring(hash.length-digits);
	var anonymityEstimate = "<p><b>WARNING</b>: The population these participants are recruited from should comprise more than <b>"+(5*Math.pow(10,digits)).toLocaleString()+"</b> individuals to ensure a mimum level of anonymity (k-anonymity = 5). Population could here refer to a country, region, particular institution, or similar, where there are publicly available list of names such as phone directories. Note that this is a probabilistic estimate only.<p>";
	document.getElementById("participantid").innerHTML = "<p style=\"color:rgb(255,0,0);\">Particpant id: "+id+"</p>"+anonymityEstimate;	
//	alert("here "+id);
	return false;
	}

function checkDuplicates(list)
	{
	var duplicates = list.filter((e, i, a) => a.indexOf(e) !== i);
	if (duplicates.length < 1 || duplicates == undefined)
		{
		return false;
		}
	document.getElementById("resultid").innerHTML = "<p style=\"color:rgb(255,0,0);\">Repeated names: "+
	duplicates+"</p>";	
	return true;
	}
	
function soundexList(names)
	{
	var x;
	var t = [];
	for (x of names)
		{
		var name = x.replace(",","");
		var parts = name.split(" ");
		parts.sort();
		var y;
		var code = "";
		for (y of parts)
			{
			code += soundex(y);
			}		
		t.push(code);
		}
	return t;
	}

function checkDuplicateSounds(names,list)
	{
	var i;
	var t = "";
	for (i = 0;i < list.length;i++)
		{
		var x = list[i];
		var s = list.slice(i+1,list.length);
		if (s.includes(x))
			{
			var j = s.indexOf(x) + 1;
			t = t + names[i] + " and "+names[j]+", ";
			}		
		}
		
	if (t.length < 1)
		{
		return false;
		}
	document.getElementById("resultid").innerHTML = "<p style=\"color:rgb(255,0,0);\">Too similar sounding names names: "+
	t+"</p>";	
	return true;
	}
	
function optimizeBlocking(names,salts,codes)
	{
	var i;
	for (i = 1; i < 6; i++)
		{
		var salt;
		for (salt of salts)
			{
			var tc = [];
			var t;
			for (t of codes)
				{
				var code = t+salt;
				var hash = "0000000"+code.hashCode();
				var id = hash.substring(hash.length-i);
				tc.push(id);
				}
			// check if the elements are unique
			if (new Set(tc).size == tc.length)
				{
//				console.log(salt+" "+i);
				document.getElementById("resultid").innerHTML = "<p style=\"color:rgb(255,0,0);\">salt: \""+
				salt+"\" with "+i+" digits.</p>";	
				return;
				}
			}
		}
	document.getElementById("resultid").innerHTML = "<p style=\"color:rgb(255,0,0);\">No valid coding found.</p>";			
	}

function optimize(names,salts,codes)
	{
	var popup = document.getElementById("myPopup");	
    popup.innerHTML = "<p>Processing 1 digits...</p>";		
	popup.style.display = "block";	
	setTimeout(analysesalts,100,names,salts,codes,5);		
	}	
	
function outputlist(names,codes,salt,i)
	{
	var txt = "<p>Do not record this list. Provided for validation purposes only.</p>";
	var j;
	for (j=0;j<names.length;j++)
		{
		var code = codes[j]+salt;
		var hash = "0000000"+code.hashCode();
		var id = hash.substring(hash.length-i);
		txt += id+": "+names[j]+"<br/>";
		}
	txt += "</p>";
	return txt;
	}
	
function analysesalts(names,salts,codes,numIteration,i=1) 
	{
	var salt;
	var popup = document.getElementById("myPopup");	
	for (salt of salts)
		{
		var tc = [];
		var t;
		for (t of codes)
			{
			var code = t+salt;
			var hash = "0000000"+code.hashCode();
			var id = hash.substring(hash.length-i);
			tc.push(id);
			}
		// check if the elements are unique
		if (new Set(tc).size == tc.length)
			{
//				console.log(salt+" "+i);
			popup.style.display = "none"; // close the popup	
			var list = outputlist(names,codes,salt,i);
			document.getElementById("resultid").innerHTML = "<p style=\"color:rgb(255,0,0);\">salt: \""+
			salt+"\" with "+i+" digits.</p>"+list;	
			return;
			}
		}		
    document.getElementById("myPopup").innerHTML = "<p>Processing "+i+" digits...</p>";	
    if (i<numIteration)
		{		
		setTimeout(analysesalts,100,names,salts,codes,numIteration,i+1);
		}
	} 	
		
	
function analyse()
	{		
	var nameArea = document.getElementById("listid").value;
	var names = nameArea.split("\n");	
	var saltArea = document.getElementById("slistid").value;
	var salts = saltArea.split("\n");	
	// check two similar names
	if (checkDuplicates(names))
		{
		return false;
		}
		
	// check two similar sounds
	var codes = soundexList(names);
	if (checkDuplicateSounds(names,codes))
		{
		return false;
		}

	// do search routine
	optimize(names,salts,codes);

	return false;
	}
	
