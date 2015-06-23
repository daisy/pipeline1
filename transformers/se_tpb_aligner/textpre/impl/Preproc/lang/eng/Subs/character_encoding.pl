#!/usr/bin/perl -w

sub character_encoding {

	my $string = shift;

	$string =~ s/Ã§/ç/g;	# c krok
	$string =~ s/Ã‚/â/g;	# a tak
	$string =~ s/Ã™/Ù/g;	# U bakåt
	$string =~ s/Ã¡/á/g;	# a med accent
	$string =~ s/Ã/Á/g;	# A accent
	$string =~ s/Ã¤/ä/g;	# a prick
	$string =~ s/Ã„/Ä/g;	# A prick
	$string =~ s/Ã¥/å/g;	# a ring
	$string =~ s/Ã…/Å/g;	# A ring
	$string =~ s/Ã©/é/g;	# e accent
	$string =~ s/Ã‰/É/g;	# E accent
	$string =~ s/Ã«/ë/g;	# e prick
	$string =~ s/Ã‹/Ë/g;	# E prick
	$string =~ s/Ã­/í/g;	# i accent
	$string =~ s/Ã/Í/g;	# I accent
	$string =~ s/Ã¯/ï/g;	# i prick
	$string =~ s/Ã/Ï/g;	# I prick
	$string =~ s/Ã³/ó/g;	# o accent
	$string =~ s/Ã“/Ó/g;	# O accent
	$string =~ s/Ã¶/ö/g;	# o prick
	$string =~ s/Ã–/Ö/g;	# O prick
	$string =~ s/Ãº/ú/g;	# u accent
	$string =~ s/Ãš/Ú/g;	# U accent
	$string =~ s/Ã¼/ü/g;	# u prick
	$string =~ s/Ãœ/Ü/g;	# U prick
	$string =~ s/Ã½/ı/g;	# y accent
	$string =~ s/Ã/İ/g;	# Y accent
	$string =~ s/Ã¿/ÿ/g;	# y prick
	$string =~ s/Ã±/ñ/g;	# n tilde
	$string =~ s/Ã‘/Ñ/g;	# N tilde
	$string =~ s/Ã¨/è/g;	# e bakåtaccent
	$string =~ s/Ãˆ/È/g;	# E bakåtaccent
	$string =~ s/Ã²/ò/g;	# o bakåtaccent
	$string =~ s/Ã’/Ò/g;	# O bakåtaccent
	$string =~ s/Ã/à/g;	# a bakåtaccent
	$string =~ s/Ã€/à/g;	# A bakåtaccent
	$string =~ s/â€™/\'/g;	# enkelfnutt
	
	
	return $string;
}
1;
