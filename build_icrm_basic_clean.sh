echo 'clean icrm package start....'

for /d /r $$i in (*src*) do rmdir /s /q "$$i"

echo 'clean icrm package end....'
