#!/usr/bin/perl -w

use locale;

#***************************************************************#
sub ort_lookup {
	
	my ($o,$pcounter) = @_;

	
	
#	print "\n\n-------------------\nPPPPP $pcounter\t\t$o\t$pos{$pcounter}\n\n";
	
	my $lc_o	=	lc($o);
	my $uc_o	=	uc($o);
	my $ucf_o	=	ucfirst($lc_o);
	
	$lc_o =~ s/Ü/ü/g;
	$uc_o =~ s/ü/Ü/g;
	$ucf_o =~ s/^ü/Ü/;
	
#	print OUT "\n\n------------\nLC: $lc_o\nUC: $uc_o\nUCF: $ucf_o\n\n";
	
	my $lookup_count=	0;
	
	#------------------------------------------------------------------#
	# Delimiter lookup	
	# Spaces are tagged as "DEL"
	#------------------------------------------------------------------#
	my $qm_o = quotemeta($o);


	if (exists($delimiter{$qm_o})) {
		$lookup_count++;
		$pos{$pcounter} 	.=	"|$delimiter{$qm_o}";
		$morphology{$pcounter}	.=	"|-";
	}

	#------------------------------------------------------------------#
	# Numeral lookup
	#------------------------------------------------------------------#
	if (
		$o =~ /^\d+$/
		||
		$o =~ /^\d+([ \,]\d\d\d)+$/
		) {
		$pos{$pcounter}		.=	"|NUM";
		$morphology{$pcounter}	.=	"|-";
	}		
	
	
	

#***************************************************************#
# For English aligning, don't do lexicon lookup!
	#------------------------------------------------------------------#
	# English lexicon lookup
	#------------------------------------------------------------------#
#	&eng_lexicon_lookup($o,$pcounter,"o");


	# Only if orthography differs from raw text.
#	&eng_lexicon_lookup($lc_o,$pcounter,"lc_o")	if ($lc_o ne $o);
#	&eng_lexicon_lookup($uc_o,$pcounter,"uc_o")	if ($uc_o ne $o);	
#	&eng_lexicon_lookup($ucf_o,$pcounter,"ucf_o")	if ($ucf_o ne $o);
		
	#------------------------------------------------------------------#
	# Acronym lookup
	#------------------------------------------------------------------#
	$found = &acronym_lookup($o,$pcounter,"o");

	# Only if orthography differs from raw text.
	$found = &acronym_lookup($lc_o,$pcounter,"lc_o")	if $found == 0;
	$found = &acronym_lookup($uc_o,$pcounter,"uc_o")	if $found == 0;	
	$found = &acronym_lookup($ucf_o,$pcounter,"ucf_o")	if $found == 0;
		
	#------------------------------------------------------------------#
	
 	#------------------------------------------------------------------#
	# Abbreviation lookup
	#------------------------------------------------------------------#
	$found = &abbreviation_lookup($o,$pcounter,"o");

	# Only if orthography differs from raw text.
	$found = &abbreviation_lookup($lc_o,$pcounter,"lc_o")	if $found == 0;
	#$found = &abbreviation_lookup($uc_o,$pcounter,"uc_o")	if $found == 0;	
	#$found = &abbreviation_lookup($ucf_o,$pcounter,"ucf_o")	if $found == 0;
		
	#------------------------------------------------------------------#
	# Name lexicon lookup
	#------------------------------------------------------------------#
###071026	&name_lexicon_lookup($o,$pcounter,"o");

	# Only if orthography differs from raw text.
###071026	&name_lexicon_lookup($ucf_o,$pcounter,"ucf_o")	if ($ucf_o ne $o);
    
	#------------------------------------------------------------------#


	$pos{$pcounter} =~ s/^UNK\|//;
	$morphology{$pcounter} =~ s/^0\|//;
	$transcription{$pcounter} =~ s/^0\|//;
	$lang{$pcounter} =~ s/^0\|//;

}
	
##***************************************************************#
sub eng_lexicon_lookup {
	
	my ($o,$pcounter,$o_type) = @_;
	
	if (exists($eng_lexicon{$o})) {
		
		my $pos;
		my $morph;

#	print "OOxx $o\n\n";
		
		if ( exists ( $eng_lexicon_tags{ $o } )) {
			($pos,$morph) 		= 	&split_pos_morph($eng_lexicon_tags{$o});

			$pos{$pcounter}			.=	"|$pos";
			$morphology{$pcounter}		.=	"|$morph";
			$transcription{$pcounter}	.=	"|$eng_lexicon{$o}";
			$lang{$pcounter}		.=	"|eng";
		}
		
	}
}
#***************************************************************#
sub name_lexicon_lookup {
	
	
	my ($o,$pcounter,$o_type) = @_;
	
	if (exists($name_lexicon_tags{$o})) {
		
		my ($pos,$morph) 		= 	&split_pos_morph($name_lexicon_tags{$o});
		$pos{$pcounter}			.=	"|$pos";	#<extra-$o_type>";
		$morphology{$pcounter}		.=	"|$morph";
# For English aligning, don't specify pronunciation.
#		$transcription{$pcounter}	.=	"|$name_lexicon{$o}";
		$lang{$pcounter}		.=	"|eng";
	}
}
##***************************************************************#
sub acronym_lookup {
	
	my ($o,$pcounter,$o_type) = @_;
	my $found = 0;
	
	my $pos;
	my $morph;

	my $ending		=	0;
	my $saved_ending	=	0;

	# Remove final endings before lookup.
	if ( $o =~ s/^(.+):($acronym_endings)$/$1/i ) {
		$ending		=	1;
		$saved_ending	=	$2;
#	} elsif ( $o =~ s/^(.+)($acronym_endings)$/$1/ ) {
#		$ending		=	1;
#		$saved_ending	=	$2;
	}

#	print "O: $o __\n";
	if (exists($acronym_list{$o})) {

		# Acronym list format: TPB # t e2: _ p e2: _ b 'e2: # ACR NOM lang:sv exp:Talboks- och punktskriftsbiblioteket
		my ($transcription,$tags) = split/\t/,$acronym_list{$o};

		
		if ( $tags =~ /^.*(ACR) (NOM|GEN).*$/ ){
			$pos = $1;
			$morph = $2;
		} else {
			$pos = "ACR";
			$morph = "NOM";
		}

		if ($ending == 1) {
			($transcription,$morph) = &add_ending($transcription,$saved_ending);
		}

		$pos{$pcounter}			.=	"|$pos";
		$type{$pcounter}		=	"ACRONYM";
		$morphology{$pcounter}		.=	"|$morph";
		$transcription{$pcounter}	.=	"|$transcription";
		$lang{$pcounter}		.=	"|eng";

		$found = 1;
	}
	
	return $found;
}
#***************************************************************#
sub abbreviation_lookup {
	
	
	my ($o,$pcounter) = @_;
	my $found = 0;

	# Remove <MERGE> from orthography (used to avoid split within multiword abbreviations as "art. nr.").
	$o =~ s/<MERGE>/ /g;
	$orthography{ $pcounter } =~ s/<MERGE>/ /g;

	#*********************************************#
	# Replace section signs
	if ( $o =~ s/\§\§/\&paratva\;/ ) {
		$orthography{ $pcounter } =~ s/\§\§/\&paratva\;/;
	}
	if ( $o =~ s/\§/\&paraett\;/ ) {
		$orthography{ $pcounter } =~ s/\§/\&paraett\;/;
	}
	#*********************************************#

	$o = quotemeta($o);

	$o = lc($o);
	
#	print "OOO $o\n";
#	my @a = split/\|/,$abbreviation_list;
#	foreach $a (@a) {
#		if ($a =~ /Chr/i) {
#			print "JA: $a\n";
#		}
#	}



	if (exists($abbreviation_list{$o}) && $pos{$pcounter} !~ /\bABBR\b/) {
	
		$pos{$pcounter}			.=	"|ABBR";
		$morphology{$pcounter}		.=	"|0";
		$found = 1;
		$type{$pcounter}		=	"ABBREVIATION";
	
		
	}
	
	return $found;
}
#***************************************************************#
#***************************************************************#
sub split_pos_morph {
	my $tag = shift;
	my $pos;
	my $morph = 0;
	my @tags = ();
	my @pos = ();
	my @morph = ();
	my $lang_check = 0;
	my $lang;
	my @lang = ();

#	print "III \t\t$tag\n\n\n";


	# Multiple tags exist.
	if (@tags = split/\|/,$tag) {

		foreach my $t (@tags) {

#			print "T. $t\n";

			# Split into tags and lang if both exist.
			if ($t =~ /^(.+)\t(.+)$/) {
				$t = $1;
				$l = $2;
				$lang_check = 1;
			}

			# Split tags into pos and morph
			if ($t =~ /^(..) (.+)$/) {
				my $p = $1;
				my $m = $2;
				push @pos,$p;
				push @morph,$m;
				if ( $lang_check == 1) {
					push @lang,$l;
				}
			} else {
				push @pos,$t;
				push @morph,"-";
				if ( $lang_check == 1) {
					push @lang,$l;
				}
			}
				
		}
		
		$pos = join"\|",@pos;
		$morph = join"\|",@morph;
		if ( $lang_check == 1) {
			$lang = join"\|",@lang;
		}
	
	# Only one tag exist.
	} else {

		if ($tag =~ /^(.+)\t(.+)$/) {
			$tag = $1;
			$lang = $2;
		}

		if ($tag =~ /^(..) (.+)$/) {
			$pos = $1;
			$morph = $2;
		} else {
			$pos = $tag;
			$morph = "-";
		}
	}
	
	
	return ($pos,$morph);
}
1;
