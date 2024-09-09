<div class="jumbotron">
      <div class="container-fluid">
        <div class="page-header">
          <h1 class="float-left">Create Leads</h1>
        </div>		
        <form method="post" action="createLead" id="createLeadForm" class="form-horizontal" name="createLeadForm" novalidate="novalidate" data-toggle="validator">
          <input type="hidden" name="duplicatingPartyId">
          <input type="hidden" name=" ">
          <input type="hidden" name="">
		  <div class="row padding-r">
          <div class="col-md-6 col-sm-6">
            <div class="form-group row has-error">
              <label for="inputEmail3" class="col-sm-4 control-label">Company Name</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name="companyName" id="Company Name" value="" placeholder="Company Name"  required>
                <div class="help-block with-errors"></div>
              </div>
            </div>
            <div class="form-group row has-error">
              <label for="inputEmail3" class="col-sm-4 control-label">First Name	</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="name" name="name" placeholder="e.g. John Smith" required> 
                <div class="help-block with-errors"></div>
              </div>
            </div>
			<div class="form-group row">
              <label  class="col-sm-4 control-label">Source</label>
              <div class="col-sm-7">			  
			<select class="ui dropdown search form-control input-sm" >
			<option value="">---Select---</option>
		 <option value="IND_AEROSPACE">Aerospace</option>   
		 <option value="IND_SOFTWARE">Computer Software</option>   
		 <option value="IND_DISTRIBUTION">Distribution</option>   
		 <option value="IND_FINANCE">Finance</option>   
		 <option value="IND_GEN_SERVICES">General Services</option>   
		 <option value="IND_MANUFACTURING">Manufacturing</option>   
		 <option value="IND_NON_PROFIT">Non-profit</option>   
		 <option value="IND_REAL_ESTATE">Real Estate</option>   
		 <option value="IND_RETAIL">Retail</option>   
		 <option value="IND_ETAILER">E-tailer</option>   
		 <option value="IND_TELECOM">Telecommunications</option>   
		 <option value="IND_PRESS">Press</option>   
			   </select> 
         </div>
            </div>

            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Salutation</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="Salutation" placeholder="Salutation">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Title</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="Title" placeholder="Title">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Annual Revenue</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="AnnualRevenue" placeholder="Annual Revenue">
              </div>
            </div>
           <div class="form-group row">
              <label  class="col-sm-4 control-label">Industry</label>
              <div class="col-sm-7">
			<select class="ui dropdown search form-control input-sm" >
			<option value="">---Select---</option>
		 <option value="IND_AEROSPACE">Aerospace</option>   
		 <option value="IND_SOFTWARE">Computer Software</option>   
		 <option value="IND_DISTRIBUTION">Distribution</option>   
		 <option value="IND_FINANCE">Finance</option>   
		 <option value="IND_GEN_SERVICES">General Services</option>   
		 <option value="IND_MANUFACTURING">Manufacturing</option>   
		 <option value="IND_NON_PROFIT">Non-profit</option>   
		 <option value="IND_REAL_ESTATE">Real Estate</option>   
		 <option value="IND_RETAIL">Retail</option>   
		 <option value="IND_ETAILER">E-tailer</option>   
		 <option value="IND_TELECOM">Telecommunications</option>   
		 <option value="IND_PRESS">Press</option>   
			   </select>                
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Ownership</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="Ownership" placeholder="Ownership">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">SIC Code</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="SICCode" placeholder="SIC Code">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Description</label>
              <div class="col-sm-7">
				<textarea name="comment" rows="3" placeholder="Description" class="form-control" ></textarea>
              </div>
            </div>
          </div>
          <div class="col-md-6 col-sm-6">
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Parent Account</label>
              <div class="col-sm-7">
                <div class="input-group">
                  <input type="text" class="form-control input-sm" placeholder="Parent Account">
                  <span class="input-group-addon">
                  <span class="glyphicon glyphicon-list-alt
                    " data-toggle="modal" data-target="#myModal">
                  </span>
                  </span>
                </div>
              </div>
            </div>
            <div class="form-group row has-error">
              <label for="inputEmail3" class="col-sm-4 control-label">Last Name	</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="lastname" placeholder="Last Name" required>
                <div class="help-block with-errors"></div>
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Marketing Campaign</label>
              <div class="col-sm-7">
                <select class="ui dropdown search form-control input-sm" >
                  <option value="10060">camp list creation sms</option>
                  <option value="10065">Test SMS Camp test</option>
                  <option value="10072">test</option>
                  <option value="10073">teste</option>
                  <option value="10081">Test SMS Campaigns06</option>
                  <option value="10100">Test SMS Camp june 06</option>
                  <option value="10110">test sms publish</option>
                  <option value="10151">Test SMS Campaigns </option>
                  <option value="10170">Test SMS Campaigns 07</option>
                  <option value="10172">sms</option>
                  <option value="10200">test campn sms</option>
                  <option value="10210">test sms12</option>
                  <option value="10357">Test Campaign122</option>
                  <option value="CATRQ_AUTOMOBILE">Automobile</option>
                  <option value="CATRQ_CAMPAIGNS">Catalog Generating Marketing Campaigns</option>
                  <option value="CATRQ_CARNDRIVER">Car and Driver</option>
                  <option value="CATRQ_ROADNTRACK">Road and Track</option>
                </select>
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Birth Date	</label>
              <div class="col-sm-7">
                <div class="input-group date" id="datetimepicker7">
                  <input type='text' class="form-control input-sm" placeholder="03/05/2018" />
                  <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
                  </span>
                </div>
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Department</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="Department" placeholder="Department">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Preferred Currency	</label>
              <div class="col-sm-7">
                <select class="ui dropdown search form-control input-sm" >
                  <option value="ADP">Andoran peseta</option>
                  <option value="AED">United Arab Emirates Dirham</option>
                  <option value="AFA">Afghani</option>
                  <option value="ALL">Albanian Lek</option>
                  <option value="AUD">Australian Dollar</option>
                  <option value="AWG">Aruban Guilder</option>
                  <option value="AZM">Azerbaijan Manat</option>
                  <option value="BAD">Bosnia-Herzogovinian Dinar</option>
                  <option value="BBD">Barbados Dollar</option>
                  <option value="BDT">Bangladesh Taka</option>
                  <option value="BGN">Bulgarian Lev</option>
                  <option value="BHD">Bahrain Dinar</option>
                  <option value="BIF">Burundi Franc</option>
                  <option value="BMD">Bermudan Dollar</option>
                  <option value="BND">Brunei Dollar</option>
                  <option value="BOB">Bolivian Boliviano</option>
                  <option value="BRL">Brazilian Real</option>
                  <option value="BRR">Brazil</option>
                  <option value="BSD">Bahaman Dollar</option>
                  <option value="BWP">Botswana Pula</option>
                  <option value="BYR">Belorussian Ruble</option>
                  <option value="BZD">Belize Dollar</option>
                  <option value="CAD">Canadian Dollar</option>
                  <option value="CDP">Santo Domiongo</option>
                  <option value="CHF">Swiss Franc</option>
                  <option value="CLP">Chilean Peso</option>
                  <option value="CNY">China</option>
                  <option value="COP">Colombian Peso</option>
                  <option value="CRC">Costa Rica Colon</option>
                  <option value="CUP">Cuban Peso</option>
                  <option value="CVE">Cape Verde Escudo</option>
                  <option value="CYP">Cyprus Pound</option>
                  <option value="CZK">Czech Krona</option>
                  <option value="DJF">Djibouti Franc</option>
                  <option value="DKK">Danish Krone</option>
                  <option value="DOP">Dominican Peso</option>
                  <option value="DRP">Dominican Republic Peso</option>
                  <option value="DZD">Algerian Dinar</option>
                  <option value="ECS">Ecuador Sucre</option>
                  <option value="EEK">Estonian Krone</option>
                  <option value="EGP">Egyptian Pound</option>
                  <option value="ETB">Ethiopian Birr</option>
                  <option value="EUR">Euro</option>
                  <option value="FJD">Fiji Dollar</option>
                  <option value="FKP">Falkland Pound</option>
                  <option value="GBP">British Pound</option>
                  <option value="GEK">Georgian Kupon</option>
                  <option value="GHC">Ghanian Cedi</option>
                  <option value="GIP">Gibraltar Pound</option>
                  <option value="GMD">Gambian Dalasi</option>
                  <option value="GNF">Guinea Franc</option>
                  <option value="GTQ">Guatemalan Quedzal</option>
                  <option value="GWP">Guinea Peso</option>
                  <option value="GYD">Guyanese Dollar</option>
                  <option value="HKD">Hong Kong Dollar</option>
                  <option value="HNL">Honduran Lempira</option>
                  <option value="HRD">Croatian Dinar</option>
                  <option value="HTG">Haitian Gourde</option>
                  <option value="HUF">Hungarian forint</option>
                  <option value="IDR">Indeonesian Rupiah</option>
                  <option value="ILS">Israeli Scheckel</option>
                  <option value="INR">Indian Rupee</option>
                  <option value="IQD">Iraqui Dinar</option>
                  <option value="IRR">Iranian Rial</option>
                  <option value="ISK">Iceland Krona</option>
                  <option value="JMD">Jamaican Dollar</option>
                  <option value="JOD">Jordanian Dinar</option>
                  <option value="JPY">Japanese Yen</option>
                  <option value="KES">Kenyan Shilling</option>
                  <option value="KHR">Cambodian Riel</option>
                  <option value="KIS">Kirghizstan Som</option>
                  <option value="KMF">Comoros Franc</option>
                  <option value="KPW">North Korean Won</option>
                  <option value="KRW">South Korean Won</option>
                  <option value="KWD">Kuwaiti Dinar</option>
                  <option value="KYD">Cayman Dollar</option>
                  <option value="KZT">Kazakhstani Tenge</option>
                  <option value="LAK">Laotian Kip</option>
                  <option value="LBP">Lebanese Pound</option>
                  <option value="LKR">Sri Lankan Rupee</option>
                  <option value="LRD">Liberian Dollar</option>
                  <option value="LSL">Lesotho Loti</option>
                  <option value="LTL">Lithuanian Lita</option>
                  <option value="LVL">Latvian Lat</option>
                  <option value="LYD">Libyan Dinar</option>
                  <option value="MAD">Moroccan Dirham</option>
                  <option value="MDL">Moldavian Lei</option>
                  <option value="MGF">Madagascan Franc</option>
                  <option value="MNT">Mongolian Tugrik</option>
                  <option value="MOP">Macao Pataca</option>
                  <option value="MRO">Mauritanian Ouguiya</option>
                  <option value="MTL">Maltese Lira</option>
                  <option value="MUR">Mauritius Rupee</option>
                  <option value="MVR">Maldive Rufiyaa</option>
                  <option value="MWK">Malawi Kwacha</option>
                  <option value="MXN">Mexican Peso (new)</option>
                  <option value="MXP">Mexican Peso (old)</option>
                  <option value="MYR">Malaysian Ringgit</option>
                  <option value="MZM">Mozambique Metical</option>
                  <option value="NGN">Nigerian Naira</option>
                  <option value="NIC">Nicaragua</option>
                  <option value="NIO">Nicaraguan Cordoba</option>
                  <option value="NIS">New Israeli Shekel</option>
                  <option value="NOK">Norwegian Krone</option>
                  <option value="NPR">Nepalese Rupee</option>
                  <option value="NZD">New Zealand Dollar</option>
                  <option value="OMR">Omani Rial</option>
                  <option value="PAB">Panamanian Balboa</option>
                  <option value="PEI">Peruvian Inti</option>
                  <option value="PEN">Peruvian Sol - New</option>
                  <option value="PES">Peruvian Sol</option>
                  <option value="PGK">Papua New Guinea Kina</option>
                  <option value="PHP">Philippine Peso</option>
                  <option value="PKR">Pakistan Rupee</option>
                  <option value="PLN">Polish Zloty</option>
                  <option value="PLZ">Poland</option>
                  <option value="PYG">Paraguayan Guarani</option>
                  <option value="QAR">Qatar Riyal</option>
                  <option value="ROL">Romanian Leu</option>
                  <option value="RUR">Russian Rouble</option>
                  <option value="RWF">Rwanda Franc</option>
                  <option value="SAR">Saudi Riyal</option>
                  <option value="SBD">Solomon Islands Dollar</option>
                  <option value="SCR">Seychelles Rupee</option>
                  <option value="SDP">Sudanese Pound</option>
                  <option value="SEK">Swedish Krona</option>
                  <option value="SGD">Singapore Dollar</option>
                  <option value="SHP">St.Helena Pound</option>
                  <option value="SLL">Leone</option>
                  <option value="SOL">Peru</option>
                  <option value="SOS">Somalian Shilling</option>
                  <option value="SRG">Surinam Guilder</option>
                  <option value="STD">Sao Tome / Principe Dobra</option>
                  <option value="SUR">Russian Ruble (old)</option>
                  <option value="SVC">El Salvador Colon</option>
                  <option value="SYP">Syrian Pound</option>
                  <option value="SZL">Swaziland Lilangeni</option>
                  <option value="THB">Thailand Baht</option>
                  <option value="TJR">Tadzhikistani Ruble</option>
                  <option value="TMM">Turkmenistani Manat</option>
                  <option value="TND">Tunisian Dinar</option>
                  <option value="TOP">Tongan Paanga</option>
                  <option value="TPE">Timor Escudo</option>
                  <option value="TRY">Turkish Lira</option>
                  <option value="TTD">Trinidad and Tobago Dollar</option>
                  <option value="TWD">New Taiwan Dollar</option>
                  <option value="TZS">Tanzanian Shilling</option>
                  <option value="UAH">Ukrainian Hryvnia</option>
                  <option value="UGS">Ugandan Shilling</option>
                  <option value="USD" selected="">United States Dollar</option>
                  <option value="UYP">Uruguayan New Peso</option>
                  <option value="UYU">Uruguay</option>
                  <option value="VEB">Venezuelan Bolivar</option>
                  <option value="VND">Vietnamese Dong</option>
                  <option value="VUV">Vanuatu Vatu</option>
                  <option value="WST">Samoan Tala</option>
                  <option value="XAF">Gabon C.f.A Franc</option>
                  <option value="XCD">East Carribean Dollar</option>
                  <option value="XOF">Benin C.f.A. Franc</option>
                  <option value="YER">Yemeni Ryal</option>
                  <option value="ZAR">South African Rand</option>
                  <option value="ZMK">Zambian Kwacha</option>
                  <option value="ZRZ">Zaire</option>
                  <option value="ZWD">Zimbabwean Dollar</option>
                </select>
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Number Of Employees</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="Employees" placeholder="Number Of Employees">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Classifications</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="Classifications" placeholder="Classifications">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Ticker Symbol</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="TickerSymbol" placeholder="Ticker Symbol">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Note</label>
              <div class="col-sm-7">
				<textarea name="comment" rows="3" placeholder="Note" class="form-control" ></textarea>
              </div>
            </div>
          </div>
          </div>
	
          <div class="clearfix"> </div>
            <div class="page-header">
              <h2 class="float-left">Contact Information</h2>
            </div>
			<div class="row padding-r">
          <div class="col-md-6 col-sm-6">
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Phone Number</label>
              <div class="col-sm-7">
                <input name="phone" placeholder="(845)555-1212" class="form-control input-sm" type="text" value="">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">E-Mail Address</label>
              <div class="col-sm-7">
                <input type="email" class="form-control input-sm" id="Email" placeholder="example@company.com">
              </div>
			  </div>
          </div>
          <div class="col-md-6 col-sm-6">
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Person to Ask For</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="Person" placeholder="Person Name">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Web Url</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="Web" placeholder="http://domain.com">
              </div>
            </div>
          </div>
          </div>		
          <div class="clearfix"> </div>
            <div class="page-header">
              <h2 class="float-left">Primary Address</h2>
            </div>
			  <div class="row padding-r">
          <div class="col-md-6 col-sm-6">
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">To Name	</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="ToName" placeholder="To Name">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Address Line 1	</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="inputEmail3" placeholder="Address Line 1">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">City</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="inputEmail3" placeholder="City">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Zip/Postal Code</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="inputEmail3" placeholder="Postal Code">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Zip/Postal Code Extension	</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="inputEmail3" placeholder="Parent Account">
              </div>
            </div>
          </div>
          <div class="col-md-6 col-sm-6">
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Attention Name	</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="inputEmail3" placeholder="Attention Name	">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Address Line 2</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="inputEmail3" placeholder="Address Line 2">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">Country</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="inputEmail3" placeholder="Country">
              </div>
            </div>
            <div class="form-group row">
              <label for="inputEmail3" class="col-sm-4 control-label">State/Province	</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="inputEmail3" placeholder="State/Province">
              </div>
            </div>
          </div>
          </div>
          <div class="col-md-12 col-sm-12">
            <div class="form-group row">
              <div class="offset-sm-2 col-sm-10">
                <a id="myWish" href="javascript:;" class="btn btn-sm btn-primary mt" >Create Lead</a>
              </div>
            </div>
<div class="alert alert-danger alert-dismissible fade in " id="success-alert">
    <button type="button" class="close" data-dismiss="alert">x</button>
    <strong>Success! </strong>
    Product have added to your wishlist.
</div>
          </div>
        </form>
      </div>
    </div>

    <!-- /.container -->
    <div id="myModal" class="modal fade" role="dialog">
      <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
          <div class="modal-header">            
            <h4 class="modal-title">Find Accounts</h4>
			<button type="reset" class="close" data-dismiss="modal">&times;</button>
          </div>
          <div class="modal-body">
            <div class="card-header">
            <form method="post" action="createLead" id="createLeadForm" class="form-horizontal" name="createLeadForm" novalidate="novalidate" data-toggle="validator">
          <input type="hidden" name="duplicatingPartyId">
          <input type="hidden" name=" ">
          <input type="hidden" name="">
		  
          <div class="row"> 
          <div class="col-md-2 col-sm-2"> 
		  <div class="form-group row mr">
			 <input type="text" class="form-control input-sm" id="" placeholder="Account ID">
          </div>
          </div>
		  <div class="col-md-2 col-sm-2">
		  <div class="form-group row mr">
			 <input type="text" class="form-control input-sm" id="" placeholder="Name">
          </div>
          </div>
		  <div class="col-md-2 col-sm-2">
		  <div class="form-group row mr">
			 <input type="text" class="form-control input-sm" id="" placeholder="Company Name">
          </div>
          </div>
		  <div class="col-md-2 col-sm-2">
		  <div class="form-group row mr">
			 <input type="text" class="form-control input-sm" id="" placeholder="Email Address">
          </div>
          </div>
		  <div class="col-md-2 col-sm-2"> 
			  <div class="form-group row mr">
			 <input type="text" class="form-control input-sm" id="" placeholder="Phone Number">
          </div>
          </div>
		  <div class="col-md-1 col-sm-1">
        <a href="#" class="btn btn-sm btn-primary navbar-dark">Find Accounts </a>
          </div>			 
          </div>			 
	  </form>
            <div class="clearfix"> </div>
          </div>
		   <div class="clearfix"> </div>
            <div class="page-header">
              <h2 class="float-left">Accounts List </h2>
            </div>
            <div class="table-responsive">
              <table id="dtable" class="table table-striped">
                <thead>
                  <tr>
                    <th>Account ID</th>
                    <th>Account Name</th>
                    <th>Status</th>					
                    <th>E-Mail Address</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td><a href="viewAccount.html">10000</a></td>
				    <td>Issam Jamai</td>
                    <td>Enabled</td>
                    <td>issamjamai@hotmail.com</td>                  
                  </tr>
				  <tr>
				  <td><a href="viewAccount.html">10001</a></td>
				  <td>Sanjeev Bhola</td>
                  <td>Enabled</td>                    
                  <td>sgffor@gmail.com</td>                  
                  </tr>
				  <tr>
				  <td><a href="viewAccount.html">10002</a></td>
				  <td>Sanjeev Bhola</td>
                  <td>Enabled</td>                  
                  <td>sgffor@gmail.com</td>               
                  </tr>				 
                </tbody>
              </table>
            </div>
		  </div>
          <div class="modal-footer">
            <button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>
	<script>
		$(document).ready (function(){
		            $("#success-alert").hide();
		            $("#myWish").click(function showAlert() {
		                $("#success-alert").fadeTo(2000, 500).slideUp(500, function(){
		               $("#success-alert").slideUp(500);
		                });   
		            });
		 });
	</script>
  