import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper
import org.ofbiz.base.util.UtilMisc;

delegator = request.getAttribute("delegator");
inputContext = new LinkedHashMap<String, Object>();

userLogin = request.getAttribute("userLogin");
partyId = request.getParameter("partyId");
context.put("userLogin", userLogin);
String isPhoneCampaignEnabled=org.fio.homeapps.util.DataUtil.isPhoneCampaignEnabled(delegator);
context.put("isPhoneCampaignEnabled", isPhoneCampaignEnabled);
String isBirthdayRemainderEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_BIRTHDAY_REMAINDER_ENABLED");
context.put("isBirthdayRemainderEnabled", isBirthdayRemainderEnabled);

if(UtilValidate.isNotEmpty(isPhoneCampaignEnabled) && isPhoneCampaignEnabled.equals("Y")) {
	if(UtilValidate.isNotEmpty(isBirthdayRemainderEnabled) && isBirthdayRemainderEnabled.equals("Y")) {
		if(UtilValidate.isNotEmpty(partyId)) {
			canDisplayBirthdayReminder = org.groupfio.common.portal.event.BirthdayRemainderEvents.canDisplayBirthdayReminder(delegator,partyId);
			context.put("canDisplayBirthdayReminder", canDisplayBirthdayReminder);
			remainingDays = org.groupfio.common.portal.event.BirthdayRemainderEvents.daysForBirthday(delegator,partyId);
			context.put("remainingDays", remainingDays);
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId),false);
			//context.put("person", person);
			if(UtilValidate.isNotEmpty(person)) {
				String name = PartyHelper.getPartyName(delegator, partyId, false);
				birthDate=person.getString("birthDate");
				context.put("name", name);
				context.put("birthDate", birthDate);
			}
		}else {
			if(org.groupfio.common.portal.event.BirthdayRemainderEvents.displayBirthdayListViewReminder(delegator, userLogin)) {
				birthdayList = org.groupfio.common.portal.event.BirthdayRemainderEvents.isBirthdayReminderListHavingToShow(delegator,userLogin);
				context.put("birthdayList", birthdayList);
			}
		}
	}
}
